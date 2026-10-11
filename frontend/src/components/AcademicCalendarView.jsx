import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import { useToast } from '../context/ToastContext';
import Card from './Card';
import Button from './Button';
import Input from './Input';
import Select from './Select';
import Badge from './Badge';
import Modal from './Modal';
import Spinner from './Spinner';
import EmptyState from './EmptyState';
import { Calendar as CalendarIcon, Plus, Trash2 } from 'lucide-react';

const AcademicCalendarView = ({ isAdmin = false }) => {
  const { addToast } = useToast();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);

  const [form, setForm] = useState({
    title: '',
    date: '',
    type: 'HOLIDAY',
    description: ''
  });

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const res = await api.get('/calendar');
      setEvents(res.data || []);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      await api.post('/admin/calendar', form);
      addToast('Academic calendar event created!', 'success');
      setShowModal(false);
      setForm({ title: '', date: '', type: 'HOLIDAY', description: '' });
      fetchEvents();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to create event', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this calendar event?')) return;
    try {
      await api.delete(`/admin/calendar/${id}`);
      addToast('Calendar event deleted', 'info');
      fetchEvents();
    } catch (err) {
      addToast('Failed to delete event', 'error');
    }
  };

  const getTypeBadge = (type) => {
    switch (type) {
      case 'HOLIDAY': return <Badge variant="danger">Holiday</Badge>;
      case 'EXAM': return <Badge variant="warning">Exam Period</Badge>;
      case 'SEMESTER_START': return <Badge variant="success">Sem Start</Badge>;
      case 'SEMESTER_END': return <Badge variant="indigo">Sem End</Badge>;
      default: return <Badge variant="secondary">{type}</Badge>;
    }
  };

  return (
    <Card title="📅 Academic Calendar & Institutional Timeline">
      {isAdmin && (
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
          <Button onClick={() => setShowModal(true)}>
            <Plus size={16} style={{ marginRight: '0.4rem' }} /> Add Event / Holiday
          </Button>
        </div>
      )}

      {loading ? (
        <Spinner label="Loading academic calendar..." />
      ) : events.length > 0 ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
          {events.map(item => (
            <div
              key={item.id}
              style={{
                padding: '1rem',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-app)',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between'
              }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                  <strong style={{ fontSize: '0.95rem' }}>{item.title}</strong>
                  {getTypeBadge(item.type)}
                </div>
                <div style={{ fontSize: '0.85rem', color: 'var(--primary-indigo)', fontWeight: 600, marginBottom: '0.4rem' }}>
                  🗓️ {item.date}
                </div>
                {item.description && (
                  <p style={{ fontSize: '0.8rem', color: 'var(--ink-muted)', margin: 0 }}>
                    {item.description}
                  </p>
                )}
              </div>

              {isAdmin && (
                <div style={{ marginTop: '0.75rem', textAlign: 'right' }}>
                  <Button variant="outline" size="sm" onClick={() => handleDelete(item.id)} style={{ color: 'var(--color-danger)', borderColor: 'var(--color-danger)' }}>
                    <Trash2 size={14} />
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <EmptyState title="No Calendar Events" description="No academic holidays or key dates logged yet." />
      )}

      {/* Add Event Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Add Academic Calendar Event">
        <form onSubmit={handleCreate}>
          <Input label="Event Title" value={form.title} onChange={e => setForm({...form, title: e.target.value})} required placeholder="e.g. Gandhi Jayanti Holiday" />
          <Input label="Date" type="date" value={form.date} onChange={e => setForm({...form, date: e.target.value})} required />
          <Select
            label="Event Category"
            value={form.type}
            onChange={e => setForm({...form, type: e.target.value})}
            options={[
              { value: 'HOLIDAY', label: 'Holiday (Classes Closed)' },
              { value: 'EXAM', label: 'Exam Period' },
              { value: 'SEMESTER_START', label: 'Semester Start' },
              { value: 'SEMESTER_END', label: 'Semester End' }
            ]}
          />
          <Input label="Description (optional)" value={form.description} onChange={e => setForm({...form, description: e.target.value})} placeholder="Optional notes or guidelines..." />
          <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>
            Save Event
          </Button>
        </form>
      </Modal>
    </Card>
  );
};

export default AcademicCalendarView;
