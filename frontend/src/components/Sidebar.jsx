import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { GraduationCap, LogOut } from 'lucide-react';
import './Sidebar.css';

const Sidebar = ({ isOpen, onClose }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getMenuItems = () => {
    if (user.role === 'ADMIN') {
      return [
        { id: 'students', label: 'Students' },
        { id: 'teachers', label: 'Teachers' },
        { id: 'subjects', label: 'Subjects' },
        { id: 'audit', label: 'Audit Trail' },
        { id: 'announcements', label: 'Announcements' }
      ];
    } else if (user.role === 'TEACHER' || user.role === 'HOD') {
      return [
        { id: 'attendance', label: 'Take Attendance' },
        { id: 'today', label: "Today's Schedule" },
        { id: 'leaves', label: 'Leave Requests' },
        { id: 'disputes', label: 'Disputes' },
        { id: 'atrisk', label: 'At-Risk Warning' },
        { id: 'history', label: 'Monthly Matrix' },
        { id: 'marks', label: 'Marks & Publishing' }
      ];
    } else if (user.role === 'STUDENT') {
      return [
        { id: 'overview', label: 'Overview' },
        { id: 'attendance', label: 'Attendance' },
        { id: 'marks', label: 'Marks' },
        { id: 'timetable', label: 'Class Timetable' },
        { id: 'leave', label: 'Leave Portal' }
      ];
    }
    return [];
  };

  const defaultTab = user.role === 'ADMIN' ? 'students' : user.role === 'TEACHER' ? 'attendance' : 'overview';
  const activeTab = searchParams.get('tab') || defaultTab;

  const handleTabClick = (tabId) => {
    setSearchParams({ tab: tabId });
    if (onClose) onClose();
  };

  return (
    <aside className={`app-sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-brand">
        <div className="sidebar-logo-box">
          <GraduationCap size={24} />
        </div>
        <span>EduTrack</span>
      </div>

      {/* User Profile & Quick Sign Out Card at Top */}
      <div style={{
        padding: '0.85rem 1.25rem',
        margin: '0 1rem 1rem 1rem',
        backgroundColor: 'rgba(255, 255, 255, 0.08)',
        borderRadius: 'var(--radius-md)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '0.5rem'
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#FFFFFF', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
            {user.name}
          </span>
          <span style={{ fontSize: '0.725rem', color: 'rgba(255, 255, 255, 0.65)' }}>
            {user.role}
          </span>
        </div>
        <button
          type="button"
          onClick={handleLogout}
          title="Sign Out"
          style={{
            background: 'rgba(239, 68, 68, 0.2)',
            border: '1px solid rgba(239, 68, 68, 0.4)',
            color: '#FCA5A5',
            padding: '0.4rem 0.65rem',
            borderRadius: 'var(--radius-sm)',
            cursor: 'pointer',
            fontSize: '0.75rem',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '0.35rem',
            transition: 'all 0.2s ease'
          }}
        >
          <LogOut size={14} />
          <span>Sign Out</span>
        </button>
      </div>

      <nav className="sidebar-menu">
        {getMenuItems().map((item) => {
          const isActive = activeTab === item.id;
          return (
            <div
              key={item.id}
              className={`sidebar-item ${isActive ? 'active' : ''}`}
              onClick={() => handleTabClick(item.id)}
            >
              <span>{item.label}</span>
            </div>
          );
        })}
      </nav>
    </aside>
  );
};

export default Sidebar;
