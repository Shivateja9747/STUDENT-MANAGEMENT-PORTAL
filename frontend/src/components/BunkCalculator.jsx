import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import Card from './Card';
import Badge from './Badge';
import Spinner from './Spinner';
import { Calculator, AlertCircle, CheckCircle2 } from 'lucide-react';

const BunkCalculator = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [target, setTarget] = useState(75);

  useEffect(() => {
    fetchBunkData();
  }, [target]);

  const fetchBunkData = async () => {
    try {
      const res = await api.get(`/student/bunk-calculator?target=${target}`);
      setData(res.data || []);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="🎓 Bunk Margin & Attendance Recovery Calculator">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
        <p style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', margin: 0 }}>
          Target attendance threshold:
        </p>
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          {[75, 80, 85, 90].map(pct => (
            <button
              key={pct}
              type="button"
              onClick={() => setTarget(pct)}
              style={{
                padding: '0.25rem 0.6rem',
                borderRadius: 'var(--radius-sm)',
                border: '1px solid var(--border-color)',
                background: target === pct ? 'var(--primary-indigo)' : 'var(--bg-app)',
                color: target === pct ? '#fff' : 'var(--ink-main)',
                fontSize: '0.8rem',
                fontWeight: 600,
                cursor: 'pointer'
              }}
            >
              {pct}%
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <Spinner label="Calculating attendance margins..." />
      ) : data.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
          {data.map(item => (
            <div
              key={item.subjectId}
              style={{
                padding: '0.85rem 1rem',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-app)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '0.5rem'
              }}
            >
              <div>
                <strong style={{ fontSize: '0.95rem' }}>{item.subjectName}</strong>
                <span style={{ fontSize: '0.8rem', color: 'var(--ink-muted)', marginLeft: '0.5rem' }}>
                  ({item.subjectCode})
                </span>
                <div style={{ fontSize: '0.8rem', color: 'var(--ink-muted)', marginTop: '0.2rem' }}>
                  Attended: {item.attendedClasses} / {item.totalClasses} classes ({item.currentPercentage.toFixed(1)}%)
                </div>
              </div>

              <div style={{ textAlign: 'right' }}>
                {item.currentPercentage >= target ? (
                  <Badge variant="success" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                    <CheckCircle2 size={12} /> Can miss {item.classesCanMiss} class(es)
                  </Badge>
                ) : (
                  <Badge variant="danger" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                    <AlertCircle size={12} /> Must attend {item.classesToAttend} class(es)
                  </Badge>
                )}
                <div style={{ fontSize: '0.75rem', color: 'var(--ink-muted)', marginTop: '0.25rem' }}>
                  {item.statusMessage}
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p style={{ color: 'var(--ink-muted)', fontSize: '0.85rem' }}>No attendance records found yet.</p>
      )}
    </Card>
  );
};

export default BunkCalculator;
