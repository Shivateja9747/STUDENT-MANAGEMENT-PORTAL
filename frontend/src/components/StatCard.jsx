import React from 'react';
import './StatCard.css';

const StatCard = ({ title, value, subtext, className = '' }) => {
  return (
    <div className={`stat-card ${className}`}>
      <div>
        <div className="stat-card-title">{title}</div>
        <div className="stat-card-value">{value}</div>
      </div>
      {subtext && <div className="stat-card-sub">{subtext}</div>}
    </div>
  );
};

export default StatCard;
