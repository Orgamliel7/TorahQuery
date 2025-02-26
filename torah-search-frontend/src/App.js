import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import SearchResults from './components/SearchResults';

import './App.css';

function App() {
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState([]); // Store search results

  const handleSearch = async (query) => {
    setSearchQuery(query);

    // Simulated search results (Replace this with your actual API call)
    const fetchedResults = [
      {
        questionText: `שאלה לדוגמא על ${query}`,
        answer: { text: 'זו תשובה לדוגמא', source: 'מקור 1', url: 'https://example.com' },
        matchScore: 0.9,
      },
    ];
    setResults(fetchedResults);
  };

  return (
    <Router>
      <div className="app" dir="rtl">
        <Routes>
          <Route path="/" element={<HomePage onSearch={handleSearch} />} />
          <Route path="/results" element={<SearchResults results={results} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
