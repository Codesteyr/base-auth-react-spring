import React, { useState } from 'react';
import UserService from '../../services/UserService';
import { useNavigate } from 'react-router-dom';
import { useStateContext } from "../../contexts/ContextProvider";

function RegistrationPage() {
    const navigate = useNavigate();
    const [error, setError] = useState('')
    const [errors, setErrors] = useState({});
    const { setUser, setToken } = useStateContext();

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setError('');

        try {
            const userData = await UserService.register(formData);

            setFormData({
                name: '',
                email: '',
                password: '',
            });

            if (userData.token) {
                setToken(userData.token); 
                setUser({ ...userData });
                navigate('/dashboard');
            } else {
                setError(userData.message);
            }
        } catch (err) {
            if (err.response && err.response.status === 400) {
                setErrors(err.response.data);
            } else {
                console.error('Error registering user:', err);
            }
        }
    };


    return (
        <div className="auth-container">
            <h2>Registration</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Name:</label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleInputChange}
                        required
                    />
                    {errors.name && <span className="error">{errors.name}</span>}
                </div>
                <div className="form-group">
                    <label>Email:</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                    />
                    {errors.email && <span className="error">{errors.email}</span>}
                </div>
                <div className="form-group">
                    <label>Password:</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleInputChange}
                        required
                    />
                    {errors.password && <span className="error">{errors.password}</span>}
                </div>
                {error && <div className="error-message">{error}</div>}
                <button type="submit">Register</button>
            </form>
        </div>
    );

}

export default RegistrationPage;
