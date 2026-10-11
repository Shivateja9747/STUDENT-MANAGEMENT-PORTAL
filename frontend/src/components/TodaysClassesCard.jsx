import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import Card from './Card';
import Button from './Button';
import Badge from './Badge';
import Spinner from './Spinner';
import EmptyState from './EmptyState';
import { Clock, MapPin, CheckCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const TodaysClassesCard = () => {
  const navigate = useNavigate();
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTodayClasses();
  }, []);

  const fetchTodayClasses = async () => {
    setLoading(true);
    try {
      const res = await api.get('/teacher/timetable/today');
      setClasses(res.data || []);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  const handleTakeAttendance = (entry) => {
    const todayStr = new Date().toISOString().split('T')[0];
    navigate(`/teacher?tab=attendance&subjectId=${entry.subjectId}&date=${todayStr}&period=${entry.period}`);
  };

  return (
    <Card title="⏰ Today's Class Schedule & One-Click Attendance">
      {loading ? (
        <Spinner label="Loading today's schedule..." />
      ) : classes.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
          {classes.map(item => (
            <div
              key={item.id}
              style={{
                padding: '1rem',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-app)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '0.75rem'
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.3rem' }}>
                  <Badge variant="indigo">Period {item.period}</Badge>
                  <strong style={{ fontSize: '1rem' }}>{item.subjectName}</strong>
                  <span style={{ fontSize: '0.85rem', color: 'var(--ink-muted)' }}>({item.subjectCode})</span>
                </div>
                <div style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', display: 'flex', gap: '1rem' }}>
                  <span>📍 {item.room || 'Room 101'}</span>
                  <span>👥 Section {item.section || 'A'}</span>
                  <span>🗓️ {item.dayOfWeek}</span>
                </div>
              </div>

              <Button onClick={() => handleTakeAttendance(item)}>
                <CheckCircle size={16} style={{ marginRight: '0.4rem' }} /> Take Attendance Now
              </Button>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState title="No Scheduled Classes Today" description="You have no classes assigned for today in the weekly timetable." />
      )}
    </Card>
  );
};

export default TodaysClassesCard;
