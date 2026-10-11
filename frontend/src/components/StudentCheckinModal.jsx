import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { QrCode, X, CheckCircle, AlertCircle, MapPin } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './StudentCheckinModal.css';

const StudentCheckinModal = ({ isOpen, onClose }) => {
  const { t } = useLanguage();
  const [token, setToken] = useState('');
  const [coords, setCoords] = useState(null);
  const [loading, setLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);

  useEffect(() => {
    if (isOpen && navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => setCoords({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        (err) => console.warn('Geolocation disabled or unavailable:', err.message)
      );
    } else {
      setToken('');
      setSuccessMsg(null);
      setErrorMsg(null);
    }
  }, [isOpen]);

  const handleSubmitCheckin = async (e) => {
    e.preventDefault();
    if (!token.trim()) {
      setErrorMsg('Please paste or scan a valid QR token.');
      return;
    }

    setLoading(true);
    setErrorMsg(null);
    setSuccessMsg(null);

    try {
      const authToken = localStorage.getItem('token');
      const payload = {
        token: token.trim(),
        latitude: coords?.lat || null,
        longitude: coords?.lng || null
      };

      const res = await axios.post('/api/student/checkin', payload, {
        headers: { Authorization: `Bearer ${authToken}` }
      });

      setSuccessMsg(res.data.message || 'Successfully checked in as PRESENT!');
      if (res.data.remarks) {
        setSuccessMsg(`Checked in! Note: ${res.data.remarks}`);
      }
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Check-in failed. Please verify QR token.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="checkin-modal-overlay" onClick={onClose}>
      <div className="checkin-modal-container" onClick={(e) => e.stopPropagation()}>
        <div className="checkin-modal-header">
          <div className="header-title">
            <QrCode size={22} />
            <span>Student QR Check-in</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="checkin-modal-body">
          {successMsg ? (
            <div className="checkin-success-card">
              <CheckCircle size={48} className="success-icon" />
              <h3>{successMsg}</h3>
              <button className="primary-btn" onClick={onClose}>Done</button>
            </div>
          ) : (
            <form onSubmit={handleSubmitCheckin} className="checkin-form">
              {errorMsg && (
                <div className="checkin-error-alert">
                  <AlertCircle size={18} />
                  <span>{errorMsg}</span>
                </div>
              )}

              <div className="form-group">
                <label>QR Token / Code</label>
                <textarea
                  rows="3"
                  className="checkin-textarea"
                  placeholder="Paste QR token payload or scan code..."
                  value={token}
                  onChange={(e) => setToken(e.target.value)}
                  required
                />
              </div>

              {coords && (
                <div className="coords-info">
                  <MapPin size={14} /> Location captured for geofence validation
                </div>
              )}

              <button type="submit" className="primary-btn" disabled={loading}>
                {loading ? t('loading') : 'Submit Check-in'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudentCheckinModal;
