import React from 'react';
import './SearchResults.css';

const SearchResults = ({ results }) => {
  if (!results || results.length === 0) {
    return <div className="no-results">לא נמצאו תוצאות</div>;
  }

  return (
    <div className="search-results">
      <h2>תוצאות חיפוש</h2>
      {results.map((result, index) => (
        <div key={index} className="result-card">
          <div className="result-question">{result.questionText}</div>
          <div className="result-answer">{result.answer.text}</div>
          <div className="result-meta">
            <span className="result-source">מקור: {result.answer.source}</span>
            <span className="result-score">התאמה: {Math.round(result.matchScore * 100)}%</span>
            {result.answer.url && (
              <a href={result.answer.url} target="_blank" rel="noopener noreferrer" className="result-link">
                קישור למקור
              </a>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default SearchResults;