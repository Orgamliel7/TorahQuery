import React, { useState } from 'react';
import './SearchBar.css';
import AutoComplete from './AutoComplete';

const SearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState('');

  // Static suggestions array
  const suggestions = [
    'שאלה 1',
    'שאלה 2',
    'שאלה 3',
    // Add more suggestions here
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSearch(query);
    }
  };

  const handleSearch = (searchQuery) => {
    setQuery(searchQuery);
    onSearch(searchQuery);
  };
  
  return (
    <div className="search-bar">
      <form onSubmit={handleSubmit}>
        <AutoComplete
          suggestions={suggestions} // passing suggestions as a prop
          onSelect={handleSearch}
          placeholder="הקלד שאלה תורנית..."
        />
        <button type="submit" className="search-button">חפש</button>
      </form>
    </div>
  );
};

export default SearchBar;
