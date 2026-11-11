
import { useEffect, useState } from 'react';
import './PatientAppointments.css';

function PatientAppointments({ patientId = 1 }) {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchAppointments = async () => {
            try {
                const response = await fetch(
                    `http://localhost:8081/api/appointments/patient/${patientId}`
                );

                if (!response.ok) {
                    throw new Error('Unable to load appointments.');
                }

                const data = await response.json();
                setAppointments(data);
            } catch (err) {
                setError(err.message || 'Unable to connect to server.');
            } finally {
                setLoading(false);
            }
        };

        fetchAppointments();
    }, [patientId]);

    if (loading) {
        return <div className="appointments-page">Loading appointments...</div>;
    }

    return (
        <div className="appointments-page">
            <div className="appointments-container">
                <h2>My Appointments</h2>
                <p className="appointments-subtitle">
                    View your MediSlot booking history.
                </p>

                {error && <p className="appointment-error">{error}</p>}

                {!error && appointments.length === 0 && (
                    <p className="no-appointments">
                        You have no appointments yet.
                    </p>
                )}

                <div className="appointment-list">
                    {appointments.map((appointment) => (
                        <div className="appointment-card" key={appointment.id}>
                            <div className="appointment-card-header">
                                <h3>{appointment.doctorName}</h3>
                                <span className="appointment-status">
                                    {appointment.status}
                                </span>
                            </div>

                            <p>
                                <strong>Appointment ID:</strong> {appointment.id}
                            </p>

                            <p>
                                <strong>Patient:</strong> {appointment.patientName}
                            </p>

                            <p>
                                <strong>Date:</strong> {appointment.appointmentDate}
                            </p>

                            <p>
                                <strong>Time:</strong> {appointment.appointmentTime}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default PatientAppointments;
