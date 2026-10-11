import React from 'react';
import './ProgressBar.css';

const ProgressBar = ({ value = 0, max = 100, showLabel = true }) => {
  const percentage = max > 0 ? Math.min(100, Math.max(0, (value / max) * 100)) : 0;

  const getColor = () => {
    if (percentage >= 75) return '#15803D'; // Green
    if (percentage >= 65) return '#D97706'; // Amber
    return '#B42318'; // Red
  };

  const color = getColor();

  return (
    <div className="progress-bar-container">
      <div className="progress-bar-track">
        <div
          className="progress-bar-fill"
          style={{ width: `${percentage}%`, backgroundColor: color }}
        />
      </div>
      {showLabel && (
        <span className="progress-bar-label" style={{ color: color }}>
          [{Math.round(percentage)}]%
        </span>
      )}
    </div>
  );
};

export default ProgressBar;
