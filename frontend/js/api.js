/**
 * MediRisk API Client Module
 * Centralized Fetch API communication layer targeting Core Java HTTP Server.
 */
const API_BASE_URL = 'http://localhost:8080/api';

const MediRiskAPI = {

  /**
   * Helper method for executing HTTP JSON requests
   */
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers
      }
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || data.message || `HTTP Error ${response.status}`);
      }
      return data;
    } catch (error) {
      console.error(`[MediRiskAPI] Error on ${endpoint}:`, error);
      if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
        throw new Error('Unable to connect to MediRisk Core Java Server (Port 8080). Please ensure the Java server is running.');
      }
      throw error;
    }
  },

  /**
   * Diagnostic Server Health Check
   */
  async checkStatus() {
    return this.request('/status', { method: 'GET' });
  },

  /**
   * User Authentication
   */
  async login(username, password) {
    return this.request('/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
  },

  /**
   * User Registration
   */
  async register(username, password, name) {
    return this.request('/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, name })
    });
  },

  /**
   * Patient Management
   */
  async getPatients() {
    return this.request('/patients', { method: 'GET' });
  },

  async createPatient(name, age, gender, contact) {
    return this.request('/patients', {
      method: 'POST',
      body: JSON.stringify({ name, age, gender, contact })
    });
  },

  async deletePatient(patientId) {
    return this.request(`/patients?id=${encodeURIComponent(patientId)}`, { method: 'DELETE' });
  },

  /**
   * Risk Assessments
   */
  async getAssessments() {
    return this.request('/assessments', { method: 'GET' });
  },

  async getAssessmentById(id) {
    return this.request(`/assessments?id=${encodeURIComponent(id)}`, { method: 'GET' });
  },

  async createAssessment(vitalsPayload) {
    return this.request('/assessments', {
      method: 'POST',
      body: JSON.stringify(vitalsPayload)
    });
  },

  async deleteAssessment(id) {
    return this.request(`/assessments?id=${encodeURIComponent(id)}`, { method: 'DELETE' });
  },

  /**
   * Health Record Metadata
   */
  async getHealthRecords() {
    return this.request('/health-records', { method: 'GET' });
  },

  async createHealthRecord(title, category, filename, sizeBytes) {
    return this.request('/health-records', {
      method: 'POST',
      body: JSON.stringify({ title, category, filename, sizeBytes })
    });
  },

  async deleteHealthRecord(id) {
    return this.request(`/health-records?id=${encodeURIComponent(id)}`, { method: 'DELETE' });
  }
};

/**
 * Global Session Utility
 */
const UserSession = {
  getUser() {
    const raw = localStorage.getItem('medirisk_user');
    return raw ? JSON.parse(raw) : null;
  },

  setUser(user) {
    localStorage.setItem('medirisk_user', JSON.stringify(user));
  },

  logout() {
    localStorage.removeItem('medirisk_user');
    window.location.href = '/index.html';
  },

  requireAuth() {
    const user = this.getUser();
    if (!user) {
      window.location.href = '/index.html';
    }
    return user;
  }
};

/**
 * Toast Notification Utility
 */
function showToast(message, type = 'info') {
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.style.borderLeftColor = type === 'error' ? '#EF4444' : type === 'success' ? '#10B981' : '#22D3EE';
  toast.innerHTML = `<span>${message}</span>`;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
}
