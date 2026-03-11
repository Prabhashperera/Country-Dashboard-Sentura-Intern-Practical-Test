import React, { useState, useEffect } from 'react';
import './App.css'; // Add basic styling here

function App() {
  const [countries, setCountries] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCountry, setSelectedCountry] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/countries')
      .then(res => res.json())
      .then(data => setCountries(data));
  }, []);

  // Typing in search box filters results
  const filteredCountries = countries.filter(country =>
    country.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ padding: '20px' }}>
      <h1>Countries Dashboard</h1>
      
      {/* Search Input */}
      <input 
        type="text" 
        placeholder="Search countries..." 
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)} 
        style={{ marginBottom: '20px', padding: '8px' }}
      />

      {/* Countries Table */}
      <table border="1" cellPadding="10" style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>Flag</th>
            <th>Name</th>
            <th>Capital</th>
            <th>Region</th>
            <th>Population</th>
          </tr>
        </thead>
        <tbody>
          {filteredCountries.map((country, index) => (
            // Click a row to show popup/modal
            <tr key={index} onClick={() => setSelectedCountry(country)} style={{ cursor: 'pointer' }}>
              <td><img src={country.flag} alt={`${country.name} flag`} width="40" /></td>
              <td>{country.name}</td>
              <td>{country.capital}</td>
              <td>{country.region}</td>
              <td>{country.population.toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Country Details Modal */}
      {selectedCountry && (
        <div style={modalOverlayStyle}>
          <div style={modalContentStyle}>
            <h2>{selectedCountry.name} Details</h2>
            <img src={selectedCountry.flag} alt="Flag" width="100" />
            <p><strong>Capital:</strong> {selectedCountry.capital}</p>
            <p><strong>Region:</strong> {selectedCountry.region}</p>
            <p><strong>Population:</strong> {selectedCountry.population.toLocaleString()}</p>
            <button onClick={() => setSelectedCountry(null)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}

// Simple inline styles for the modal
const modalOverlayStyle = {
  position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
  backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex',
  justifyContent: 'center', alignItems: 'center'
};
const modalContentStyle = {
  backgroundColor: 'white', padding: '20px', borderRadius: '8px', minWidth: '300px'
};

export default App;