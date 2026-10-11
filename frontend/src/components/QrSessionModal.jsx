import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { QrCode, X, RefreshCw, Users, Shield, MapPin } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './QrSessionModal.css';

const QrSessionModal = ({ isOpen, onClose, subjects = [] }) => {
  const { t } = useLanguage();
  const [selectedSubject, setSelectedSubject] = useState(subjects[0]?.id || '');
  const [period, setPeriod] = useState(1);
  const [geofenceEnabled, setGeofenceEnabled] = useState(false);
  const [userLocation, setUserLocation] = useState(null);

  const [activeSession, setActiveSession] = useState(null);
  const [token, setToken] = useState('');
  const [countdown, setCountdown] = useState(30);
  const [attendances, setAttendances] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (subjects.length > 0 && !selectedSubject) {
      setSelectedSubject(subjects[0].id);
    }
  }, [subjects]);

  // Handle Geolocation query
  const handleGeofenceToggle = (e) => {
    const checked = e.target.checked;
    setGeofenceEnabled(checked);
    if (checked && navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => setUserLocation({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        (err) => console.warn('Geolocation warning:', err.message)
      );
    }
  };

  const startSession = async () => {
    if (!selectedSubject) {
      setError('Please select a subject');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const authToken = localStorage.getItem('token');
      const payload = {
        subjectId: selectedSubject,
        period: period,
        geofenceEnabled: geofenceEnabled,
        centerLat: userLocation?.lat || null,
        centerLng: userLocation?.lng || null,
        radiusMeters: 100.0
      };
      const res = await axios.post('/api/teacher/qr-session/start', payload, {
        headers: { Authorization: `Bearer ${authToken}` }
      });
      setActiveSession(res.data);
      fetchToken(res.data.id);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to start QR session.');
    } finally {
      setLoading(false);
    }
  };

  const fetchToken = async (sessionId) => {
    try {
      const authToken = localStorage.getItem('token');
      const res = await axios.get(`/api/teacher/qr-session/${sessionId}/token`, {
        headers: { Authorization: `Bearer ${authToken}` }
      });
      setToken(res.data.token);
      setCountdown(30);
      fetchAttendances(sessionId);
    } catch (err) {
      console.error('Token fetch error:', err);
    }
  };

  const fetchAttendances = async (sessionId) => {
    try {
      const authToken = localStorage.getItem('token');
      const res = await axios.get(`/api/teacher/qr-session/${sessionId}/attendances`, {
        headers: { Authorization: `Bearer ${authToken}` }
      });
      setAttendances(res.data || []);
    } catch (err) {
      console.error('Attendance fetch error:', err);
    }
  };

  const closeSession = async () => {
    if (!activeSession) return;
    try {
      const authToken = localStorage.getItem('token');
      await axios.post(`/api/teacher/qr-session/${activeSession.id}/close`, {}, {
        headers: { Authorization: `Bearer ${authToken}` }
      });
      setActiveSession(null);
      setToken('');
      setAttendances([]);
    } catch (err) {
      console.error('Close session error:', err);
    }
  };

  // Timer loop for 30s token rotation
  useEffect(() => {
    let timer;
    if (activeSession) {
      timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            fetchToken(activeSession.id);
            return 30;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [activeSession]);

  if (!isOpen) return null;

  return (
    <div className="qr-modal-overlay" onClick={onClose}>
      <div className="qr-modal-container" onClick={(e) => e.stopPropagation()}>
        <div className="qr-modal-header">
          <div className="header-title">
            <QrCode size={22} />
            <span>{t('startQrSession')}</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="qr-modal-body">
          {error && <div className="qr-error-alert">{error}</div>}

          {!activeSession ? (
            <div className="qr-setup-form">
              <div className="form-group">
                <label>Select Subject</label>
                <select value={selectedSubject} onChange={(e) => setSelectedSubject(e.target.value)}>
                  {subjects.map((s) => (
                    <option key={s.id} value={s.id}>{s.code} - {s.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Period / Class Hour</label>
                <input type="number" min="1" max="10" value={period} onChange={(e) => setPeriod(parseInt(e.target.value) || 1)} />
              </div>

              <div className="form-checkbox-group">
                <label>
                  <input type="checkbox" checked={geofenceEnabled} onChange={handleGeofenceToggle} />
                  <span>Enable Geofence (100m location restriction)</span>
                </label>
                {geofenceEnabled && userLocation && (
                  <span className="location-info"><MapPin size={14} /> Lat: {userLocation.lat.toFixed(4)}, Lng: {userLocation.lng.toFixed(4)}</span>
                )}
              </div>

              <button className="primary-btn" onClick={startSession} disabled={loading}>
                {loading ? t('loading') : t('startQrSession')}
              </button>
            </div>
          ) : (
            <div className="qr-active-session">
              <div className="qr-display-box">
                <div className="qr-code-wrapper">
                  <img
                    src={`https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(token)}`}
                    alt="Live Rotating QR Code"
                    className="qr-img"
                  />
                </div>
                <div className="qr-timer-badge">
                  <RefreshCw size={16} className="spinning" />
                  <span>Rotating token in <strong>{countdown}s</strong></span>
                </div>
              </div>

              <div className="live-attendances-list">
                <div className="attendances-header">
                  <Users size={18} />
                  <span>Live Student Check-ins ({attendances.length})</span>
                </div>
                <div className="attendances-scroll">
                  {attendances.length === 0 ? (
                    <div className="empty-state">Waiting for students to scan...</div>
                  ) : (
                    attendances.map((a) => (
                      <div key={a.id} className="attendance-chip">
                        <span className="student-name">{a.student?.name} ({a.student?.rollNo})</span>
                        <span className="chip-status">{a.status}</span>
                      </div>
                    ))
                  )}
                </div>
              </div>

              <button className="danger-btn" onClick={closeSession}>Close Session</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default QrSessionModal;
