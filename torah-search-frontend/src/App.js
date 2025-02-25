import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import SearchResultsPage from './pages/SearchResultsPage'; // Assuming you have a results page
import './App.css';

function App() {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (query) => {
    setSearchQuery(query);
  };

  return (
    <Router>
      <div className="app" dir="rtl">
        <Routes>
          <Route
            path="/"
            element={<HomePage onSearch={handleSearch} />}
          />
          <Route
            path="/results"
            element={<SearchResultsPage query={searchQuery} />}
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
