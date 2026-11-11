
import { useEffect, useState } from 'react';
import './AppointmentBooking.css';

function AppointmentBooking({ patientId = 1 }) {
  const [doctors, setDoctors] = useState([]);
  const [specialization, setSpecialization] = useState('');
  const [appointmentDate, setAppointmentDate] = useState('');
  const [preferredTime, setPreferredTime] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetch('http://localhost:8081/api/appointments/doctors')
      .then(response => {
        if (!response.ok) throw new Error('Unable to load doctors');
        return response.json();
      })
      .then(data => setDoctors(data))
      .catch(() => setError('Could not load doctors. Please check the backend.'));
  }, []);

  const specializations = [
    ...new Set(doctors.map(doctor => doctor.specialization))
  ];

  const selectedDoctors = doctors.filter(
    doctor => doctor.specialization === specialization
  );

  const availableSlots = [
    ...new Set(selectedDoctors.flatMap(doctor => doctor.availableSlots || []))
  ].sort();

  const handleBooking = async (event) => {
    event.preventDefault();
    setMessage('');
    setError('');

    if (!specialization || !appointmentDate || !preferredTime) {
      setError('Please fill in all appointment details.');
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(
        'http://localhost:8081/api/appointments/book',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            patientId,
            specialization,
            appointmentDate,
            preferredTime
          })
        }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Booking failed.');
      }

      setMessage(
        `Appointment confirmed! Appointment ID: ${data.id}, Doctor: ${data.doctorName}, Time: ${data.appointmentTime}`
      );

      setPreferredTime('');
    } catch (err) {
      setError(err.message || 'Unable to book appointment.');
    } finally {
      setLoading(false);
    }
  };

  const today = new Date().toLocaleDateString('en-CA');

  return (
    <section className="booking-section">
      <div className="booking-card">
        <span className="booking-tag">MEDISLOT APPOINTMENTS</span>

        <h2>Book Your Appointment</h2>

        <p className="booking-description">
          Choose your preferred doctor, date and available time slot.
        </p>

        <form onSubmit={handleBooking}>
          <label htmlFor="specialization">Select Specialization</label>

          <select
            id="specialization"
            value={specialization}
            onChange={(e) => {
              setSpecialization(e.target.value);
              setPreferredTime('');
            }}
            required
          >
            <option value="">Choose specialization</option>
            {specializations.map(item => (
              <option key={item} value={item}>{item}</option>
            ))}
          </select>

          {specialization && (
            <div className="doctor-info">
              <strong>Available Doctors</strong>
              {selectedDoctors.map(doctor => (
                <p key={doctor.id}>
                  Dr. {doctor.name.replace(/^Dr\.?\s*/i, '')}
                </p>
              ))}
            </div>
          )}

          <label htmlFor="appointmentDate">Appointment Date</label>

          <input
            id="appointmentDate"
            type="date"
            min={today}
            value={appointmentDate}
            onChange={(e) => setAppointmentDate(e.target.value)}
            required
          />

          <label htmlFor="preferredTime">Available Time Slot</label>

          <select
            id="preferredTime"
            value={preferredTime}
            onChange={(e) => setPreferredTime(e.target.value)}
            required
            disabled={!specialization}
          >
            <option value="">Choose a time slot</option>
            {availableSlots.map(slot => (
              <option key={slot} value={slot}>{slot}</option>
            ))}
          </select>

          <button type="submit" disabled={loading}>
            {loading ? 'Booking...' : 'Confirm Appointment'}
          </button>
        </form>

        {message && <div className="booking-success">{message}</div>}
        {error && <div className="booking-error">{error}</div>}
      </div>
    </section>
  );
}

export default AppointmentBooking;
