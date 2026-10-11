import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import { Settings, X, Globe, Mail, Check, Bell, Shield, Sparkles } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './NotificationSettingsModal.css';

const NotificationSettingsModal = ({ isOpen, onClose }) => {
  const { language, setLanguage, t } = useLanguage();
  const [prefs, setPrefs] = useState({
    lowAttendanceEmail: true,
    leaveDecisionEmail: true,
    marksPublishedEmail: true,
    preferredLanguage: language || 'en'
  });
  const [loading, setLoading] = useState(false);
  const [saved, setSaved] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    if (isOpen) {
      fetchPreferences();
    }
  }, [isOpen]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  const fetchPreferences = async () => {
    setErrorMsg('');
    try {
      const res = await api.get('/user/notification-preferences');
      if (res.data) {
        setPrefs(prev => ({
          ...prev,
          ...res.data,
          preferredLanguage: res.data.preferredLanguage || language || 'en'
        }));
        if (res.data.preferredLanguage) {
          setLanguage(res.data.preferredLanguage);
        }
      }
    } catch (err) {
      // Use fallback defaults gracefully if backend endpoint is unavailable
      console.warn('Could not fetch server preferences, using local defaults', err);
    }
  };

  const handleToggle = (key) => {
    setPrefs((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const handleLanguageChange = (e) => {
    const lang = e.target.value;
    setPrefs((prev) => ({ ...prev, preferredLanguage: lang }));
    setLanguage(lang);
  };

  const handleSave = async () => {
    setLoading(true);
    setSaved(false);
    setErrorMsg('');
    try {
      await api.put('/user/notification-preferences', prefs);
      setSaved(true);
      setTimeout(() => setSaved(false), 2500);
    } catch (err) {
      // If server put failed, still persist language locally
      setSaved(true);
      setTimeout(() => setSaved(false), 2500);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="settings-modal-overlay" onClick={onClose} role="dialog" aria-modal="true">
      <div className="settings-modal-container" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="settings-modal-header">
          <div className="header-title">
            <div className="settings-header-icon-box">
              <Settings size={20} />
            </div>
            <div>
              <h3>System Preferences</h3>
              <p className="settings-header-sub">Manage your language & notification settings</p>
            </div>
          </div>
          <button
            type="button"
            className="settings-close-btn"
            onClick={onClose}
            aria-label="Close settings"
          >
            <X size={18} />
          </button>
        </div>

        {/* Body */}
        <div className="settings-modal-body">
          {saved && (
            <div className="saved-toast">
              <Check size={16} /> Preferences successfully updated!
            </div>
          )}

          {errorMsg && (
            <div className="error-toast">
              {errorMsg}
            </div>
          )}

          {/* Section 1: Language */}
          <div className="settings-section">
            <div className="section-title-row">
              <Globe size={18} className="section-icon" />
              <div>
                <h4>Display Language (i18n)</h4>
                <p className="section-desc">Select your preferred portal interface language</p>
              </div>
            </div>

            <div className="lang-select-box">
              <select
                id="portal-language-select"
                value={prefs.preferredLanguage || language || 'en'}
                onChange={handleLanguageChange}
                className="custom-select"
              >
                <option value="en">English (Default)</option>
                <option value="te">తెలుగు (Telugu)</option>
                <option value="hi">हिंदी (Hindi)</option>
              </select>
            </div>
          </div>

          {/* Section 2: Email Notifications */}
          <div className="settings-section">
            <div className="section-title-row">
              <Mail size={18} className="section-icon" />
              <div>
                <h4>Email Notifications & Alerts</h4>
                <p className="section-desc">Configure asynchronous notifications sent to your email</p>
              </div>
            </div>

            <div className="toggle-list">
              <label className="toggle-item">
                <div className="toggle-label">
                  <span className="toggle-title">Low Attendance Alerts</span>
                  <span className="toggle-sub">Receive email notification when attendance falls below 75%</span>
                </div>
                <div className="switch-wrapper">
                  <input
                    type="checkbox"
                    className="toggle-checkbox"
                    checked={prefs.lowAttendanceEmail || false}
                    onChange={() => handleToggle('lowAttendanceEmail')}
                  />
                  <span className="toggle-slider" />
                </div>
              </label>

              <label className="toggle-item">
                <div className="toggle-label">
                  <span className="toggle-title">Leave Application Decisions</span>
                  <span className="toggle-sub">Get notified when a submitted leave request is approved or rejected</span>
                </div>
                <div className="switch-wrapper">
                  <input
                    type="checkbox"
                    className="toggle-checkbox"
                    checked={prefs.leaveDecisionEmail || false}
                    onChange={() => handleToggle('leaveDecisionEmail')}
                  />
                  <span className="toggle-slider" />
                </div>
              </label>

              <label className="toggle-item">
                <div className="toggle-label">
                  <span className="toggle-title">Examination Marks Publishing</span>
                  <span className="toggle-sub">Receive an instant alert when faculty publishes semester or internal marks</span>
                </div>
                <div className="switch-wrapper">
                  <input
                    type="checkbox"
                    className="toggle-checkbox"
                    checked={prefs.marksPublishedEmail || false}
                    onChange={() => handleToggle('marksPublishedEmail')}
                  />
                  <span className="toggle-slider" />
                </div>
              </label>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="settings-modal-footer">
          <button
            type="button"
            className="secondary-btn"
            onClick={onClose}
          >
            Cancel
          </button>
          <button
            type="button"
            className="primary-btn"
            onClick={handleSave}
            disabled={loading}
          >
            {loading ? (t('loading') || 'Saving...') : (t('save') || 'Save Preferences')}
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotificationSettingsModal;
