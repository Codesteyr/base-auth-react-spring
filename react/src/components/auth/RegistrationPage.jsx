import React, { useState } from "react";
import UserService from "../../services/UserService";
import { useNavigate } from "react-router-dom";
import { useStateContext } from "../../contexts/ContextProvider";
import { Link } from 'react-router-dom';

function RegistrationPage() {
    const navigate = useNavigate();
    const { setUser, setToken } = useStateContext();
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        agreeToRules: false,
    });
    const [error, setError] = useState("");
    const [errors, setErrors] = useState({});
    const [isRulesModalOpen, setRulesModalOpen] = useState(false);

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({ ...formData, [name]: type === "checkbox" ? checked : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        setError("");

        if (formData.password !== formData.confirmPassword) {
            setErrors({ confirmPassword: "Passwords do not match" });
            return;
        }

        if (!formData.agreeToRules) {
            setErrors({ agreeToRules: "You must agree to the rules" });
            return;
        }

        try {
            const userData = await UserService.register(formData);
            if (userData.token) {
                setToken(userData.token);
                setUser({ ...userData });
                navigate("/dashboard");
            } else {
                setError(userData.message);
            }
        } catch (err) {
            if (err.response && err.response.status === 400) {
                setErrors(err.response.data);
            } else {
                setError("An error occurred during registration");
            }
        }
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-container">
                <div className="auth-title">
                    <h2>Registration</h2>
                    <Link to="/login">Login</Link>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Name:</label>
                        <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
                        {errors.name && <span className="error">{errors.name}</span>}
                    </div>
                    <div className="form-group">
                        <label>Email:</label>
                        <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                        {errors.email && <span className="error">{errors.email}</span>}
                    </div>
                    <div className="form-group">
                        <label>Password:</label>
                        <input type="password" name="password" value={formData.password} onChange={handleInputChange} required />
                        {errors.password && <span className="error">{errors.password}</span>}
                    </div>
                    <div className="form-group">
                        <label>Confirm Password:</label>
                        <input type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleInputChange} required />
                        {errors.confirmPassword && <span className="error">{errors.confirmPassword}</span>}
                    </div>
                    <div className="form-group">
                        <label>
                            <input type="checkbox" name="agreeToRules" id="agreeToRules" checked={formData.agreeToRules} onChange={handleInputChange} />
                            <label htmlFor="agreeToRules">I agree to the rules</label>
                        </label>
                        {errors.agreeToRules && <span className="error">{errors.agreeToRules}</span>}
                        <button type="button" onClick={() => setRulesModalOpen(true)}>View Rules</button>
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <button type="submit">Register</button>
                </form>

                {isRulesModalOpen && (
                    <div className="modal">
                        <div className="modal-content">
                            <h3>Rules</h3>
                            <p>Here are the rules...</p>
                            <button onClick={() => setRulesModalOpen(false)}>Close</button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default RegistrationPage;