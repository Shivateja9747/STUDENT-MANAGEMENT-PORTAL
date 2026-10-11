import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { GraduationCap, AlertCircle, Award, CheckCircle, BookOpen } from 'lucide-react';
import './PublicParentPortal.css';

const PublicParentPortal = () => {
  const { token } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchPortalData();
  }, [token]);

  const fetchPortalData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(`/api/public/parent-portal/${token}`);
      setData(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid, expired, or revoked share link.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="parent-portal-container">
        <div className="portal-loading">Loading Parent & Guardian Academic Portal...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="parent-portal-container">
        <div className="portal-error-card">
          <AlertCircle size={48} className="error-icon" />
          <h2>Access Denied</h2>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="parent-portal-container">
      <header className="portal-header">
        <div className="brand">
          <GraduationCap size={28} />
          <span>EduTrack Parent Portal</span>
        </div>
        <div className="expiry-notice">
          Read-only Access Link • Expires: {new Date(data.expiresAt).toLocaleDateString()}
        </div>
      </header>

      <main className="portal-content">
        <div className="student-profile-card">
          <div className="student-main-info">
            <h2>{data.studentName}</h2>
            <div className="info-tags">
              <span>Roll No: <strong>{data.rollNo}</strong></span>
              <span>Department: <strong>{data.department}</strong></span>
              <span>Semester: <strong>{data.semester}</strong></span>
            </div>
          </div>
          <div className="overall-att-badge">
            <span className="att-pct">{data.overallAttendancePercentage}%</span>
            <span className="att-label">Overall Attendance</span>
          </div>
        </div>

        <section className="portal-section">
          <h3><BookOpen size={20} /> Subject-wise Attendance Breakdown</h3>
          <div className="attendance-cards-grid">
            {data.subjectAttendances && data.subjectAttendances.map((sub) => (
              <div key={sub.subjectId} className="subject-att-card">
                <div className="card-title">{sub.subjectCode} - {sub.subjectName}</div>
                <div className="card-stats">
                  <span>Attended: {sub.presentClasses}/{sub.totalClasses}</span>
                  <span className={`pct ${sub.lowAttendance ? 'low' : 'good'}`}>{sub.percentage}%</span>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="portal-section">
          <h3><Award size={20} /> Published Examination Marks Transcript</h3>
          {data.publishedMarks && data.publishedMarks.length === 0 ? (
            <div className="no-marks-msg">No published exam marks available yet.</div>
          ) : (
            <div className="marks-table-wrapper">
              <table className="portal-marks-table">
                <thead>
                  <tr>
                    <th>Subject Code</th>
                    <th>Subject Name</th>
                    <th>Exam Type</th>
                    <th>Score Obtained</th>
                    <th>Percentage</th>
                    <th>Grade</th>
                  </tr>
                </thead>
                <tbody>
                  {data.publishedMarks && data.publishedMarks.map((m) => (
                    <tr key={m.id}>
                      <td><strong>{m.subjectCode}</strong></td>
                      <td>{m.subjectName}</td>
                      <td>{m.examType}</td>
                      <td>{m.absent ? 'ABSENT' : `${m.marksObtained} / ${m.maxMarks}`}</td>
                      <td>{m.percentage}%</td>
                      <td><span className="grade-badge">{m.grade}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default PublicParentPortal;
