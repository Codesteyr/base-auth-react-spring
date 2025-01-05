import React from 'react';
import './App.css'
import { ContextProvider } from './contexts/ContextProvider';
import AppRoutes from './routes';

export default function App() {
  return (
    <ContextProvider>
      <AppRoutes />
    </ContextProvider>
  );
}
