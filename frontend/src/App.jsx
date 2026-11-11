
import { useState } from 'react';
import './App.css';
import PatientRegistration from './PatientRegistration';
import AppointmentBooking from './AppointmentBooking';

function App() {
  const [showRegistration, setShowRegistration] = useState(false);
  const [showBooking, setShowBooking] = useState(false);

  const showHome = () => {
    setShowRegistration(false);
    setShowBooking(false);
  };

  const openRegistration = () => {
    setShowRegistration(true);
    setShowBooking(false);
  };

  const openBooking = () => {
    setShowBooking(true);
    setShowRegistration(false);
  };

  return (
    <div className="app">

      {/* Navigation */}
      <nav className="navbar">
        <div
          className="logo"
          onClick={showHome}
          style={{ cursor: 'pointer' }}
        >
          <span className="logo-icon">✚</span>
          MediSlot
        </div>

        <div className="nav-links">
          <a href="#home" onClick={showHome}>Home</a>
          <a href="#features" onClick={showHome}>Features</a>
          <a href="#how-it-works" onClick={showHome}>How It Works</a>
          <a href="#booking" onClick={openBooking}>Book Appointment</a>
        </div>

        <div className="nav-buttons">
          <button
            className="login-btn"
            onClick={openRegistration}
          >
            Login
          </button>

          <button
            className="signup-btn"
            onClick={openRegistration}
          >
            Sign Up
          </button>
        </div>
      </nav>

      {/* Patient Registration */}
      {showRegistration ? (
        <PatientRegistration />
      ) : showBooking ? (

        /* Appointment Booking */
        <div id="booking">
          <AppointmentBooking patientId={1} />
        </div>

      ) : (

        /* Homepage */
        <>

          {/* Hero Section */}
          <section className="hero" id="home">

            <div className="hero-content">
              <span className="tagline">
                YOUR HEALTH, OUR PRIORITY
              </span>

              <h1>
                Healthcare Scheduling,
                <span> Made Simple.</span>
              </h1>

              <p>
                Book appointments with trusted doctors without
                the hassle. Find available slots, manage your
                appointments and save valuable time with MediSlot.
              </p>

              <div className="hero-buttons">
                <button
                  className="primary-btn"
                  onClick={openBooking}
                >
                  Book an Appointment →
                </button>

                <button
                  className="secondary-btn"
                  onClick={() =>
                    document.getElementById('features')?.scrollIntoView({
                      behavior: 'smooth'
                    })
                  }
                >
                  Explore Services
                </button>
              </div>

              <div className="hero-stats">
                <div>
                  <h3>Easy</h3>
                  <p>Online Booking</p>
                </div>

                <div>
                  <h3>Smart</h3>
                  <p>Slot Allocation</p>
                </div>

                <div>
                  <h3>24/7</h3>
                  <p>Appointment Access</p>
                </div>
              </div>
            </div>

            <div className="hero-visual">
              <div className="medical-card">
                <div className="medical-icon">✚</div>

                <h2>Your Health Matters</h2>

                <p>
                  Find the right doctor and schedule
                  your appointment with ease.
                </p>

                <div className="appointment-preview">
                  <span>✓</span>
                  Smart Appointment Scheduling
                </div>

                <div className="appointment-preview">
                  <span>✓</span>
                  Real-time Slot Availability
                </div>

                <div className="appointment-preview">
                  <span>✓</span>
                  Easy Appointment Management
                </div>
              </div>
            </div>

          </section>

          {/* Features Section */}
          <section className="features" id="features">

            <div className="section-heading">
              <span>OUR FEATURES</span>

              <h2>Everything You Need in One Place</h2>

              <p>
                A smarter way to manage your healthcare appointments.
              </p>
            </div>

            <div className="feature-grid">

              <div className="feature-card">
                <div className="feature-icon">📅</div>
                <h3>Easy Booking</h3>
                <p>
                  Find doctors and book available appointment
                  slots quickly and conveniently.
                </p>
              </div>

              <div className="feature-card">
                <div className="feature-icon">⚡</div>
                <h3>Smart Scheduling</h3>
                <p>
                  Intelligent algorithms help manage availability
                  and appointment requests efficiently.
                </p>
              </div>

              <div className="feature-card">
                <div className="feature-icon">🩺</div>
                <h3>Doctor Management</h3>
                <p>
                  View doctor schedules and manage
                  appointments in one place.
                </p>
              </div>

              <div className="feature-card">
                <div className="feature-icon">📊</div>
                <h3>Smart Analytics</h3>
                <p>
                  Monitor appointment statistics and
                  waiting-time information.
                </p>
              </div>

            </div>
          </section>

          {/* How It Works */}
          <section className="how-it-works" id="how-it-works">

            <div className="section-heading">
              <span>SIMPLE PROCESS</span>
              <h2>How MediSlot Works</h2>
            </div>

            <div className="steps">

              <div className="step">
                <div className="step-number">01</div>
                <h3>Find a Doctor</h3>
                <p>Search for doctors by specialization.</p>
              </div>

              <div className="step">
                <div className="step-number">02</div>
                <h3>Select a Slot</h3>
                <p>Choose a suitable date and available time.</p>
              </div>

              <div className="step">
                <div className="step-number">03</div>
                <h3>Confirm Booking</h3>
                <p>Submit your request and manage your appointment.</p>
              </div>

            </div>
          </section>

          {/* Footer */}
          <footer className="footer">

            <div className="logo">
              <span className="logo-icon">✚</span>
              MediSlot
            </div>

            <p>
              Making healthcare scheduling smarter and simpler.
            </p>

            <p className="copyright">
              © 2026 MediSlot. All rights reserved.
            </p>

          </footer>

        </>
      )}

    </div>
  );
}

export default App;
