import React, { useState } from 'react';
import SearchBar from '../components/SearchBar';
import SearchResults from '../components/SearchResults';
import { searchQuestions } from '../api/searchApi';
import './HomePage.css';

const HomePage = () => {
  const [searchResults, setSearchResults] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = async (query) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const data = await searchQuestions(query);
      setSearchResults(data.results);
    } catch (err) {
      setError('אירעה שגיאה בחיפוש. אנא נסה שוב מאוחר יותר.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="home-page">
      <header className="header">
        <h1>מנוע חיפוש תשובות תורניות</h1>
        <p>חפש שאלות ותשובות בנושאים תורניים ממקורות מגוונים</p>
      </header>
      
      <main>
        <SearchBar onSearch={handleSearch} />
        
        {isLoading && <div className="loading">טוען תוצאות...</div>}
        
        {error && <div className="error">{error}</div>}
        
        {searchResults && <SearchResults results={searchResults} />}
      </main>
      
      <footer className="footer">
        <p>© {new Date().getFullYear()} מנוע חיפוש תשובות תורניות - כל הזכויות שמורות</p>
      </footer>
    </div>
  );
};

export default HomePage;        