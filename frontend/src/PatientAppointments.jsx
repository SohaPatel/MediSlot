
import { useEffect, useState } from 'react';
import './PatientAppointments.css';

function PatientAppointments({ patientId = 1 }) {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [cancelError, setCancelError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [cancellingId, setCancellingId] = useState(null);

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

    // Cancel appointment
    const handleCancelAppointment = async (appointmentId) => {
        if (cancellingId !== null) {
            return;
        }

        const confirmed = window.confirm(
            'Are you sure you want to cancel this appointment?'
        );

        if (!confirmed) {
            return;
        }

        setCancellingId(appointmentId);
        setCancelError('');
        setSuccessMessage('');

        try {
            const response = await fetch(
                `http://localhost:8081/api/appointments/${appointmentId}/cancel`,
                {
                    method: 'PATCH'
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.error || data.message || 'Unable to cancel appointment.'
                );
            }

            // Update appointment status without refreshing the page
            setAppointments((previousAppointments) =>
                previousAppointments.map((appointment) =>
                    appointment.id === appointmentId
                        ? { ...appointment, status: 'CANCELLED' }
                        : appointment
                )
            );

            setSuccessMessage(
                `Appointment #${appointmentId} cancelled successfully!`
            );

        } catch (err) {
            setCancelError(
                err.message || 'Unable to connect to server.'
            );
        } finally {
            setCancellingId(null);
        }
    };

    if (loading) {
        return (
            <div className="appointments-page">
                Loading appointments...
            </div>
        );
    }

    return (
        <div className="appointments-page">
            <div className="appointments-container">

                <h2>My Appointments</h2>

                <p className="appointments-subtitle">
                    View your MediSlot booking history.
                </p>

                {error && (
                    <p className="appointment-error">{error}</p>
                )}

                {successMessage && (
                    <p className="cancellation-success">
                        {successMessage}
                    </p>
                )}

                {cancelError && (
                    <p className="cancellation-error">
                        {cancelError}
                    </p>
                )}

                {!error && appointments.length === 0 && (
                    <p className="no-appointments">
                        You have no appointments yet.
                    </p>
                )}

                <div className="appointment-list">

                    {appointments.map((appointment) => (
                        <div
                            className="appointment-card"
                            key={appointment.id}
                        >

                            <div className="appointment-card-header">

                                <h3>{appointment.doctorName}</h3>

                                <span
                                    className={`appointment-status ${
                                        appointment.status === 'CANCELLED'
                                            ? 'cancelled-status'
                                            : 'confirmed-status'
                                    }`}
                                >
                                    {appointment.status}
                                </span>

                            </div>

                            <p>
                                <strong>Appointment ID:</strong>{' '}
                                {appointment.id}
                            </p>

                            <p>
                                <strong>Patient:</strong>{' '}
                                {appointment.patientName}
                            </p>

                            <p>
                                <strong>Date:</strong>{' '}
                                {appointment.appointmentDate}
                            </p>

                            <p>
                                <strong>Time:</strong>{' '}
                                {appointment.appointmentTime}
                            </p>

                            {appointment.status === 'CONFIRMED' && (
                                <button
                                    className="cancel-appointment-btn"
                                    onClick={() =>
                                        handleCancelAppointment(appointment.id)
                                    }
                                    disabled={cancellingId !== null}
                                >
                                    {cancellingId === appointment.id
                                        ? 'Cancelling...'
                                        : 'Cancel Appointment'}
                                </button>
                            )}

                        </div>
                    ))}

                </div>

            </div>
        </div>
    );
}

export default PatientAppointments;
