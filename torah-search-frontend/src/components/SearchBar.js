import React, { useState } from 'react';
import './SearchBar.css';
import AutoComplete from './AutoComplete';


const SearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState('');

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
          suggestions={suggestions}
          onSelect={handleSearch}
          placeholder="הקלד שאלה תורנית..."
        />
        <button type="submit" className="search-button">חפש</button>
      </form>
    </div>
  );
};

export default SearchBar;