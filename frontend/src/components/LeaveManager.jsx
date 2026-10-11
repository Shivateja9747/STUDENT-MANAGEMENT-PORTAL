import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import { useToast } from '../context/ToastContext';
import Card from './Card';
import Button from './Button';
import Input from './Input';
import Badge from './Badge';
import Modal from './Modal';
import Spinner from './Spinner';
import EmptyState from './EmptyState';
import { Calendar, Check, X, FileText } from 'lucide-react';

const LeaveManager = ({ isTeacherOrHod = false }) => {
  const { addToast } = useToast();
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);

  const [form, setForm] = useState({
    startDate: '',
    endDate: '',
    reason: '',
    attachmentUrl: ''
  });

  const [reviewModal, setReviewModal] = useState(null);
  const [reviewComment, setReviewComment] = useState('');

  useEffect(() => {
    fetchLeaves();
  }, [isTeacherOrHod]);

  const fetchLeaves = async () => {
    setLoading(true);
    try {
      const endpoint = isTeacherOrHod ? '/teacher/leave/pending' : '/student/leave';
      const res = await api.get(endpoint);
      setLeaves(res.data || []);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      await api.post('/student/leave', form);
      addToast('Leave application submitted successfully!', 'success');
      setShowModal(false);
      setForm({ startDate: '', endDate: '', reason: '', attachmentUrl: '' });
      fetchLeaves();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to submit leave', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleReview = async (approve) => {
    if (!reviewModal) return;
    setSubmitLoading(true);
    try {
      await api.post(`/teacher/leave/${reviewModal.id}/review`, {
        approve,
        comment: reviewComment
      });
      addToast(`Leave ${approve ? 'approved' : 'rejected'} successfully!`, approve ? 'success' : 'info');
      setReviewModal(null);
      setReviewComment('');
      fetchLeaves();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to review leave', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <Card title={isTeacherOrHod ? "📋 Leave Approval Inbox" : "✈️ My Leave Applications"}>
      {!isTeacherOrHod && (
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
          <Button onClick={() => setShowModal(true)}>
            + Apply for Leave
          </Button>
        </div>
      )}

      {loading ? (
        <Spinner label="Loading leave applications..." />
      ) : leaves.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
          {leaves.map(item => (
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
                  <strong style={{ fontSize: '0.95rem' }}>
                    {isTeacherOrHod ? `${item.studentName} (${item.studentRollNo})` : `Leave Application #${item.id}`}
                  </strong>
                  <Badge variant={item.status === 'APPROVED' ? 'success' : item.status === 'REJECTED' ? 'danger' : 'warning'}>
                    {item.status}
                  </Badge>
                </div>
                <div style={{ fontSize: '0.85rem', color: 'var(--ink-muted)' }}>
                  Duration: <strong>{item.startDate}</strong> to <strong>{item.endDate}</strong>
                </div>
                <p style={{ fontSize: '0.85rem', color: 'var(--ink-main)', margin: '0.4rem 0 0.2rem 0' }}>
                  Reason: {item.reason}
                </p>
                {item.reviewerComment && (
                  <div style={{ fontSize: '0.8rem', color: 'var(--primary-indigo)', fontStyle: 'italic', marginTop: '0.2rem' }}>
                    Remark: "{item.reviewerComment}" (by {item.reviewedBy || 'Reviewer'})
                  </div>
                )}
              </div>

              {isTeacherOrHod && item.status === 'PENDING' && (
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <Button size="sm" variant="success" onClick={() => { setReviewModal(item); setReviewComment(''); }}>
                    <Check size={14} style={{ marginRight: '0.2rem' }} /> Approve
                  </Button>
                  <Button size="sm" variant="danger" onClick={() => { setReviewModal(item); setReviewComment(''); }}>
                    <X size={14} style={{ marginRight: '0.2rem' }} /> Reject
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title={isTeacherOrHod ? "No Pending Leaves" : "No Leave Applications"}
          description={isTeacherOrHod ? "All student leave applications have been reviewed." : "Click '+ Apply for Leave' to submit a request."}
        />
      )}

      {/* Apply Leave Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Apply for Leave">
        <form onSubmit={handleSubmit}>
          <Input label="Start Date" type="date" value={form.startDate} onChange={e => setForm({...form, startDate: e.target.value})} required />
          <Input label="End Date" type="date" value={form.endDate} onChange={e => setForm({...form, endDate: e.target.value})} required />
          <Input label="Reason for Leave" value={form.reason} onChange={e => setForm({...form, reason: e.target.value})} required />
          <Input label="Attachment Document URL (optional)" placeholder="https://example.com/doc.pdf" value={form.attachmentUrl} onChange={e => setForm({...form, attachmentUrl: e.target.value})} />
          <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>
            Submit Leave Application
          </Button>
        </form>
      </Modal>

      {/* Review Modal */}
      <Modal isOpen={!!reviewModal} onClose={() => setReviewModal(null)} title={`Review Leave - ${reviewModal?.studentName}`}>
        <div>
          <p style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', marginBottom: '1rem' }}>
            Request from <strong>{reviewModal?.startDate}</strong> to <strong>{reviewModal?.endDate}</strong> for: "{reviewModal?.reason}"
          </p>
          <Input label="Reviewer Remark / Comment" value={reviewComment} onChange={e => setReviewComment(e.target.value)} placeholder="Add optional remarks..." />
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem' }}>
            <Button fullWidth variant="success" loading={submitLoading} onClick={() => handleReview(true)}>
              Approve Leave
            </Button>
            <Button fullWidth variant="danger" loading={submitLoading} onClick={() => handleReview(false)}>
              Reject Leave
            </Button>
          </div>
        </div>
      </Modal>
    </Card>
  );
};

export default LeaveManager;
