import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { AlertTriangle, TrendingDown, CheckCircle, ShieldAlert, Award } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './AtRiskPanel.css';

const AtRiskPanel = ({ role, user }) => {
  const { t } = useLanguage();
  const [data, setData] = useState(null);
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchRiskData();
  }, [role]);

  const fetchRiskData = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem('token');
      const endpoint = role === 'STUDENT' ? '/api/student/at-risk' : '/api/teacher/at-risk';
      const res = await axios.get(endpoint, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (role === 'STUDENT') {
        setData(res.data);
      } else {
        setList(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load risk analysis data.');
    } finally {
      setLoading(false);
    }
  };

  const getBadgeClass = (level) => {
    if (level === 'High') return 'risk-badge high';
    if (level === 'Medium') return 'risk-badge medium';
    return 'risk-badge low';
  };

  if (loading) {
    return <div className="at-risk-loading">{t('loading')}</div>;
  }

  if (error) {
    return <div className="at-risk-error"><AlertTriangle size={20} /> {error}</div>;
  }

  if (role === 'STUDENT' && data) {
    return (
      <div className="at-risk-student-card">
        <div className="at-risk-header">
          <div className="title-group">
            <ShieldAlert size={24} className="risk-icon" />
            <div>
              <h3>{t('atRiskTitle')}</h3>
              <p className="subtitle">Real-time attendance trend and academic performance evaluation</p>
            </div>
          </div>
          <span className={getBadgeClass(data.riskLevel)}>{data.riskLevel} Risk ({data.riskScore}/100)</span>
        </div>

        <div className="risk-score-bar-container">
          <div className="risk-score-bar" style={{ width: `${data.riskScore}%` }}></div>
        </div>

        <div className="risk-details-grid">
          <div className="detail-box">
            <span className="label">Attendance</span>
            <span className="value">{data.attendancePercentage}%</span>
          </div>
          <div className="detail-box">
            <span className="label">Marks Average</span>
            <span className="value">{data.marksPercentage}%</span>
          </div>
          <div className="detail-box">
            <span className="label">Failed Exams</span>
            <span className="value">{data.failedExamsCount}</span>
          </div>
        </div>

        {data.trendMessage && (
          <div className="trend-banner">
            <TrendingDown size={16} /> <span>{data.trendMessage}</span>
          </div>
        )}

        <div className="reasons-section">
          <h4>Analysis & Recommendations</h4>
          <ul>
            {data.reasons && data.reasons.map((r, i) => (
              <li key={i}>{r}</li>
            ))}
          </ul>
        </div>
      </div>
    );
  }

  return (
    <div className="at-risk-panel-container">
      <div className="panel-header">
        <h3>Department At-Risk Early Warning Panel</h3>
        <span className="count-badge">{list.length} Students Evaluated</span>
      </div>

      <div className="risk-table-wrapper">
        <table className="risk-table">
          <thead>
            <tr>
              <th>Roll No</th>
              <th>Student Name</th>
              <th>Dept & Sec</th>
              <th>Attendance</th>
              <th>Marks %</th>
              <th>Risk Score</th>
              <th>Level</th>
              <th>Primary Reason</th>
            </tr>
          </thead>
          <tbody>
            {list.map((item) => (
              <tr key={item.studentId}>
                <td><strong>{item.rollNo}</strong></td>
                <td>{item.studentName}</td>
                <td>{item.department} - {item.section}</td>
                <td>{item.attendancePercentage}%</td>
                <td>{item.marksPercentage}%</td>
                <td><strong>{item.riskScore} / 100</strong></td>
                <td><span className={getBadgeClass(item.riskLevel)}>{item.riskLevel}</span></td>
                <td className="reason-cell">{item.reasons?.[0] || 'Good standing'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AtRiskPanel;
