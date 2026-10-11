import React, { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { useAuth } from '../../context/AuthContext';
import Input from '../../components/Input';
import Button from '../../components/Button';
import { GraduationCap } from 'lucide-react';
import './LoginPage.css';

const LoginPage = () => {
  const isDemoMode = import.meta.env.VITE_DEMO_MODE === 'true';
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { user, login } = useAuth();
  const navigate = useNavigate();

  // Redirect if logged in
  if (user) {
    if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
    if (user.role === 'TEACHER') return <Navigate to="/teacher" replace />;
    if (user.role === 'STUDENT') return <Navigate to="/student" replace />;
  }

  const performLogin = async (userUsername, userPassword) => {
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { username: userUsername, password: userPassword });
      const data = response.data;
      login(data);

      if (data.role === 'ADMIN') navigate('/admin');
      else if (data.role === 'TEACHER') navigate('/teacher');
      else if (data.role === 'STUDENT') navigate('/student');
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Login failed. Please check network connection.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!username.trim()) {
      setError('Please enter your username');
      return;
    }
    if (!password) {
      setError('Please enter your password');
      return;
    }
    performLogin(username, password);
  };

  const handleRoleClick = (role) => {
    let u = 'admin', p = 'admin123';
    if (role === 'Teacher') { u = 'teacher1'; p = 'teacher123'; }
    else if (role === 'Student') { u = 'student1'; p = 'student123'; }
    
    setUsername(u);
    setPassword(p);
    performLogin(u, p);
  };

  return (
    <div className="login-container">
      {/* Left Brand Panel */}
      <div className="login-brand-side">
        <div className="login-brand-header">
          <div className="login-brand-logo-icon">
            <GraduationCap size={26} />
          </div>
          <span className="login-brand-name">EduTrack</span>
        </div>

        <div className="login-brand-body">
          <h1 className="login-brand-headline">
            Attendance and marks,<br />in one clear place.
          </h1>
          <p className="login-brand-tagline">
            Teachers record in seconds. Students see where they stand. Admins keep everything organised.
          </p>

          <div className="login-role-chips">
            <button type="button" className="role-chip" onClick={() => handleRoleClick('Admin')}>⚡ Admin</button>
            <button type="button" className="role-chip" onClick={() => handleRoleClick('Teacher')}>⚡ Teacher</button>
            <button type="button" className="role-chip" onClick={() => handleRoleClick('Student')}>⚡ Student</button>
          </div>
        </div>

        <div className="login-brand-footer">
          Student Attendance & Academic Performance Management System
        </div>
      </div>

      {/* Right Form Side */}
      <div className="login-form-side">
        <div className="login-form-box">
          <h2 className="login-form-title">Welcome back</h2>
          <p className="login-form-subtitle">Sign in to continue to your dashboard.</p>

          {error && (
            <div style={{
              backgroundColor: 'var(--bg-danger)',
              border: '1px solid var(--color-danger)',
              color: 'var(--color-danger)',
              padding: '0.75rem 1rem',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.875rem',
              marginBottom: '1.25rem'
            }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="Username"
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />

            <Input
              label="Password"
              type={showPassword ? 'text' : 'password'}
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <div className="login-options-row">
              <label className="login-checkbox-label">
                <input
                  type="checkbox"
                  checked={showPassword}
                  onChange={(e) => setShowPassword(e.target.checked)}
                />
                <span>Show password</span>
              </label>
              <a href="#help" onClick={(e) => { e.preventDefault(); alert('Please contact system administrator for account credentials.'); }} className="login-help-link">
                Need help?
              </a>
            </div>

            <Button
              type="submit"
              variant="primary"
              loading={loading}
              fullWidth
              style={{ height: '48px', fontSize: '1rem', fontWeight: 600 }}
            >
              Sign in
            </Button>
          </form>

          {/* Quick Demo Login Options */}
          <div className="quick-demo-section">
            <div className="quick-demo-divider">
              <span>ONE-CLICK AUTOMATIC LOGIN</span>
            </div>
            <div className="quick-demo-grid">
              <button
                type="button"
                className="quick-demo-btn"
                onClick={() => handleRoleClick('Admin')}
                disabled={loading}
              >
                ⚡ Admin
              </button>
              <button
                type="button"
                className="quick-demo-btn"
                onClick={() => handleRoleClick('Teacher')}
                disabled={loading}
              >
                ⚡ Teacher
              </button>
              <button
                type="button"
                className="quick-demo-btn"
                onClick={() => handleRoleClick('Student')}
                disabled={loading}
              >
                ⚡ Student
              </button>
            </div>
          </div>

          <p className="login-footer-text">
            Accounts are created by your administrator.
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
