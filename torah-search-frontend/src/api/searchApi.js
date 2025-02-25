import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const searchQuestions = async (query) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/search`, {
      params: { query }
    });
    return response.data;
  } catch (error) {
    console.error('Error searching questions:', error);
    throw error;
  }
};

export const getQuestions = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/questions`);
    return response.data;
  } catch (error) {
    console.error('Error fetching questions:', error);
    throw error;
  }
};

export const getAnswers = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/answers`);
    return response.data;
  } catch (error) {
    console.error('Error fetching answers:', error);
    throw error;
  }
};