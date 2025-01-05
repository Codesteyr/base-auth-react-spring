import React from 'react';
import { Navigate, createBrowserRouter, RouterProvider } from 'react-router-dom';
import DefaultLayout from './components/layouts/DefaultLayout';
import GuestLayout from './components/layouts/GuestLayout';
import Dashboard from './components/views/Dashboard';
import Site from './components/views/Site';
import RegistrationPage from './components/auth/RegistrationPage';
import LoginPage from './components/auth/LoginPage';

const router = createBrowserRouter([
    {
        path: '/',
        element: <DefaultLayout />,
        children: [
            { path: '/', element: <Navigate to='/dashboard' /> },
            { path: '/dashboard', element: <Dashboard /> },
            { path: '/site', element: <Site /> },
        ],
    },
    {
        path: '/',
        element: <GuestLayout />,
        children: [
            { path: '/site', element: <Site /> },
            { path: '/register', element: <RegistrationPage /> },
            { path: '/login', element: <LoginPage /> },
        ],
    },
    { path: '*', element: <Navigate to='/site' /> },
]);

export default function AppRoutes() {
    return <RouterProvider router={router} />;
}