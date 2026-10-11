import React from 'react';

const Card = ({ children, title, subtitle, action, style = {}, className = '' }) => {
  return (
    <div
      className={className}
      style={{
        backgroundColor: 'var(--bg-card)',
        border: '1px solid var(--border-color)',
        borderRadius: 'var(--radius-md)',
        padding: 'var(--space-lg)',
        boxShadow: 'var(--shadow-md)',
        marginBottom: 'var(--space-lg)',
        ...style
      }}
    >
      {(title || action) && (
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: subtitle ? '0.25rem' : '1rem'
        }}>
          {title && <h3 style={{ fontSize: '1.15rem', fontWeight: 600, color: 'var(--text-main)' }}>{title}</h3>}
          {action && <div>{action}</div>}
        </div>
      )}
      {subtitle && (
        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1rem' }}>
          {subtitle}
        </p>
      )}
      {children}
    </div>
  );
};

export default Card;
