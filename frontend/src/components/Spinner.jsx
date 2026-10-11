import React from 'react';
import { Loader2 } from 'lucide-react';

const Spinner = ({ size = 32, label = 'Loading...' }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', padding: '2rem' }}>
      <Loader2 size={size} color="var(--primary-indigo)" style={{ animation: 'spin 1s linear infinite' }} />
      {label && <span style={{ color: 'var(--ink-muted)', fontSize: '0.9rem', fontWeight: 500 }}>{label}</span>}
    </div>
  );
};

export default Spinner;
