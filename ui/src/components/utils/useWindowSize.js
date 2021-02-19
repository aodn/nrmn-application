import {useState, useEffect} from 'react';

export default function useWindowSize() {
  const isClient = typeof window === 'object';

  const getSize = () => {
    return {
      width: isClient ? window.innerWidth : undefined,
      height: isClient ? window.innerHeight : undefined
    };
  };

  const [windowSize, setWindowSize] = useState(getSize);
  useEffect(() => {
    // eslint-disable-next-line react-hooks/exhaustive-deps

    if (!isClient) {
      return false;
    }

    function handleResize() {
      setWindowSize(getSize());
    }

    window.addEventListener('resize', handleResize);

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return windowSize;
}
