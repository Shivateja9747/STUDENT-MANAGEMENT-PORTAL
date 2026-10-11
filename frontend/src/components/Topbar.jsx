import React from 'react';
import { useAuth } from '../context/AuthContext';
import Badge from './Badge';
import { Menu } from 'lucide-react';
import './Topbar.css';

const Topbar = ({ title, subtitle, onToggleSidebar }) => {
  const { user } = useAuth();

  if (!user) return null;

  const getRoleVariant = (role) => {
    if (role === 'ADMIN') return 'admin';
    if (role === 'TEACHER') return 'teacher';
    return 'student';
  };

  return (
    <div className="topbar">
      <div style={{ display: 'flex', alignItems: 'flex-start' }}>
        <button className="mobile-menu-toggle" onClick={onToggleSidebar} aria-label="Toggle navigation drawer">
          <Menu size={24} />
        </button>
        <div className="topbar-title-area">
          <h1 className="topbar-title">{title}</h1>
          {subtitle && <p className="topbar-subtitle">{subtitle}</p>}
        </div>
      </div>

      <div className="topbar-user-area">
        <Badge variant={getRoleVariant(user.role)}>
          {user.role}
        </Badge>
        <span className="topbar-username">
          {user.username}
        </span>
      </div>
    </div>
  );
};

export default Topbar;
