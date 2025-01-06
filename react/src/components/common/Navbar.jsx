import React from 'react';
import { Link } from 'react-router-dom';
import UserService from '../../services/UserService';
import { useNavigate } from "react-router-dom";
import { useStateContext } from '../../contexts/ContextProvider';


function Navbar() {
    const {user, token, setToken } = useStateContext();


    const handleLogout = () => {
        const confirmDelete = window.confirm('Are you sure you want to logout this user?');
        if (confirmDelete) {
            UserService.logout();
            setToken("");
        }
    };


    return (
        <nav>
            <ul>
                {<li><Link to="/login">Login</Link></li>}
                {<li><Link to="/register">Register</Link></li>}
                {token && <li><Link to="/dashboard">Dashboard</Link></li>}
                {token && <li><Link to="/" onClick={handleLogout}>Logout : {user.name}</Link></li>}
            </ul>
        </nav>
    );
}

export default Navbar;
