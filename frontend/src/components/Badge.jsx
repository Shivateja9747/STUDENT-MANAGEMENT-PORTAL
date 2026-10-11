import React from 'react';
import './Badge.css';

const Badge = ({ children, variant = 'gray', className = '' }) => {
  const variantClass = `badge-${variant.toLowerCase()}`;
  return (
    <span className={`badge ${variantClass} ${className}`}>
      {children}
    </span>
  );
};

export default Badge;
