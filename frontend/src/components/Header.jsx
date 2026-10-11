import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { Menu, User, Bell, Key, LogOut, QrCode, Settings } from 'lucide-react';
import Badge from './Badge';
import ChangePasswordModal from './ChangePasswordModal';
import NotificationSettingsModal from './NotificationSettingsModal';
import QrSessionModal from './QrSessionModal';
import api from '../api/axiosInstance';
import './Header.css';

const Header = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [showNotifs, setShowNotifs] = useState(false);
  const [showChangePass, setShowChangePass] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const [showTeacherQr, setShowTeacherQr] = useState(false);
  const notifRef = useRef(null);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', 'light');
    document.body.classList.remove('dark-theme');
    localStorage.removeItem('edutrack_theme');
  }, []);

  useEffect(() => {
    if (!user) return;
    const fetchNotifs = async () => {
      try {
        const res = await api.get('/announcements');
        setNotifications(res.data || []);
      } catch (err) {
        // Silent catch
      }
    };
    fetchNotifs();
  }, [user]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target)) {
        setShowNotifs(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  if (!user) return null;


  const getRoleVariant = (role) => {
    if (role === 'ADMIN') return 'danger';
    if (role === 'TEACHER' || role === 'HOD') return 'indigo';
    return 'success';
  };

  return (
    <>
      <header className="top-header">
        <div className="top-header-left">
          <button
            type="button"
            onClick={onToggleSidebar}
            aria-label="Toggle Navigation Menu"
            className="mobile-menu-btn"
          >
            <Menu size={22} />
          </button>
          <span className="header-app-title">
            EduTrack &bull; {user.role === 'ADMIN' ? 'Admin Portal' : (user.role === 'TEACHER' || user.role === 'HOD') ? 'Faculty Portal' : 'Student Portal'}
          </span>
        </div>

        <div className="header-actions">
          {/* QR Action Button */}
          {(user.role === 'TEACHER' || user.role === 'HOD') && (
            <button
              type="button"
              className="header-icon-btn qr-trigger"
              onClick={() => setShowTeacherQr(true)}
              title="Start QR Session"
            >
              <QrCode size={18} />
            </button>
          )}

          {/* Settings / i18n & Notification Preferences */}
          <button
            type="button"
            className="header-icon-btn"
            onClick={() => setShowSettings(true)}
            title="Preferences & i18n Language Settings"
          >
            <Settings size={19} />
          </button>

          {/* Notifications Bell */}
          <div className="notif-wrapper" ref={notifRef}>
            <button
              type="button"
              className="header-icon-btn"
              onClick={() => setShowNotifs(!showNotifs)}
              title="Notifications"
            >
              <Bell size={19} />
              {notifications.length > 0 && (
                <span className="notif-badge-dot">{notifications.length}</span>
              )}
            </button>

            {showNotifs && (
              <div className="notif-dropdown">
                <div className="notif-header">
                  <span>Announcements & Alerts</span>
                  <span className="notif-count-label">{notifications.length} total</span>
                </div>
                <div className="notif-body">
                  {notifications.length === 0 ? (
                    <div className="notif-empty">No active notifications</div>
                  ) : (
                    notifications.map(item => (
                      <div key={item.id} className="notif-item">
                        <div className="notif-item-title">{item.title}</div>
                        <div className="notif-item-content">{item.content}</div>
                        <div className="notif-item-date">
                          {new Date(item.createdAt).toLocaleDateString(undefined, {
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>

          {/* User Profile Info & Password Action */}
          <div className="header-user">
            <div className="header-avatar" title={user.name}>
              <User size={18} />
            </div>
            <div className="header-user-info">
              <span className="header-user-name">{user.name}</span>
              <span className="header-user-role">@{user.username}</span>
            </div>
            <Badge variant={getRoleVariant(user.role)}>
              {user.role}
            </Badge>

            <button
              type="button"
              className="header-icon-btn"
              onClick={() => setShowChangePass(true)}
              title="Change Password"
              style={{ marginLeft: '0.25rem' }}
            >
              <Key size={17} />
            </button>
            <button
              type="button"
              className="header-icon-btn danger-hover"
              onClick={logout}
              title="Sign Out"
            >
              <LogOut size={17} />
            </button>
          </div>
        </div>
      </header>

      <ChangePasswordModal
        isOpen={showChangePass}
        onClose={() => setShowChangePass(false)}
      />

      <NotificationSettingsModal
        isOpen={showSettings}
        onClose={() => setShowSettings(false)}
      />

      <QrSessionModal
        isOpen={showTeacherQr}
        onClose={() => setShowTeacherQr(false)}
      />
    </>
  );
};

export default Header;
