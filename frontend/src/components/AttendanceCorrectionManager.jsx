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
import { AlertTriangle, Check, X } from 'lucide-react';

const AttendanceCorrectionManager = ({ isTeacherOrHod = false }) => {
  const { addToast } = useToast();
  const [corrections, setCorrections] = useState([]);
  const [loading, setLoading] = useState(true);

  const [reviewModal, setReviewModal] = useState(null);
  const [reviewComment, setReviewComment] = useState('');
  const [submitLoading, setSubmitLoading] = useState(false);

  useEffect(() => {
    fetchCorrections();
  }, [isTeacherOrHod]);

  const fetchCorrections = async () => {
    setLoading(true);
    try {
      const endpoint = isTeacherOrHod ? '/teacher/corrections/pending' : '/student/corrections';
      const res = await api.get(endpoint);
      setCorrections(res.data || []);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  const handleReview = async (approve) => {
    if (!reviewModal) return;
    setSubmitLoading(true);
    try {
      await api.post(`/teacher/corrections/${reviewModal.id}/review`, {
        approve,
        comment: reviewComment
      });
      addToast(`Dispute ${approve ? 'approved & updated' : 'rejected'}!`, approve ? 'success' : 'info');
      setReviewModal(null);
      setReviewComment('');
      fetchCorrections();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to review dispute', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <Card title={isTeacherOrHod ? "⚖️ Attendance Dispute & Correction Inbox" : "⚖️ My Attendance Correction Disputes"}>
      {loading ? (
        <Spinner label="Loading correction disputes..." />
      ) : corrections.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
          {corrections.map(item => (
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
                    {item.subjectName} ({item.attendanceDate})
                  </strong>
                  <Badge variant={item.status === 'APPROVED' ? 'success' : item.status === 'REJECTED' ? 'danger' : 'warning'}>
                    {item.status}
                  </Badge>
                </div>
                <div style={{ fontSize: '0.85rem', color: 'var(--ink-muted)' }}>
                  Student: <strong>{item.studentName} ({item.studentRollNo})</strong> | Current: <Badge variant="secondary">{item.currentStatus}</Badge> ➔ Requested: <Badge variant="indigo">{item.requestedStatus}</Badge>
                </div>
                <p style={{ fontSize: '0.85rem', color: 'var(--ink-main)', margin: '0.4rem 0 0.2rem 0' }}>
                  Reason: {item.reason}
                </p>
                {item.reviewerComment && (
                  <div style={{ fontSize: '0.8rem', color: 'var(--primary-indigo)', fontStyle: 'italic', marginTop: '0.2rem' }}>
                    Remark: "{item.reviewerComment}"
                  </div>
                )}
              </div>

              {isTeacherOrHod && item.status === 'PENDING' && (
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <Button size="sm" variant="success" onClick={() => { setReviewModal(item); setReviewComment(''); }}>
                    <Check size={14} style={{ marginRight: '0.2rem' }} /> Approve & Fix
                  </Button>
                  <Button size="sm" variant="danger" onClick={() => { setReviewModal(item); setReviewComment(''); }}>
                    <X size={14} style={{ marginRight: '0.2rem' }} /> Reject Dispute
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <EmptyState
          title={isTeacherOrHod ? "No Active Disputes" : "No Correction Requests"}
          description={isTeacherOrHod ? "There are no pending attendance correction disputes to resolve." : "No attendance dispute requests submitted."}
        />
      )}

      {/* Review Modal */}
      <Modal isOpen={!!reviewModal} onClose={() => setReviewModal(null)} title={`Resolve Dispute - ${reviewModal?.studentName}`}>
        <div>
          <p style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', marginBottom: '1rem' }}>
            Dispute for <strong>{reviewModal?.subjectName}</strong> on <strong>{reviewModal?.attendanceDate}</strong>: Change from <strong>{reviewModal?.currentStatus}</strong> to <strong>{reviewModal?.requestedStatus}</strong>.
          </p>
          <Input label="Teacher Remark / Explanation" value={reviewComment} onChange={e => setReviewComment(e.target.value)} placeholder="Add reason for approval or rejection..." />
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem' }}>
            <Button fullWidth variant="success" loading={submitLoading} onClick={() => handleReview(true)}>
              Approve Change
            </Button>
            <Button fullWidth variant="danger" loading={submitLoading} onClick={() => handleReview(false)}>
              Reject Dispute
            </Button>
          </div>
        </div>
      </Modal>
    </Card>
  );
};

export default AttendanceCorrectionManager;
