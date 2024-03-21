import React, {useEffect} from 'react';
import {Link, Typography} from '@mui/material';
import Box from '@mui/material/Box';



const Homepage = () => {

  useEffect(() => {
    document.title = 'National Reef Monitoring Network';
  }, []);

  return (
      <>
        <Box flexGrow={11} m={5} maxWidth={900} alignSelf="center">
          <Typography variant="h2" color="textPrimary">
            NRMN - Data Portal
          </Typography>
          <Typography variant="h5" color="textSecondary">
            Underwater visual census data on shallow reefs
          </Typography>
          <Box mt={5}>
            <Typography color="textPrimary" variant="body1" component="div">
              <p>
                <strong>Australia{"'"}s Integrated Marine Observing System (IMOS)</strong> is enabled by the National Collaborative Research
                Infrastructure Strategy (NCRIS). It is operated by a consortium of institutions as an unincorporated joint venture, with the{' '}
                <Link href="https://www.utas.edu.au/">University of Tasmania</Link> as Lead Agent.
              </p>
              <p>NRMN is an IMOS sub-facility. Its roles:</p>
              <ul>
                <li>
                  Collate, clean, store and make rapidly available, all data obtained during shallow reef surveys conducted by the National
                  Reef Monitoring Network
                </li>
                <li>Foster formal links with external monitoring programs for integrated use and outcomes</li>
                <li>Make consistent data available at the national (and international) scale, for shallow reefs</li>
              </ul>

              <p>
                <strong>
                  Public data from the NRMN is available via the{' '}
                  <Link rel="noreferrer" target="_blank" href="https://portal.aodn.org.au/">
                    AODN Portal
                  </Link>
                  .
                </strong>
              </p>
            </Typography>
          </Box>
        </Box>
        <Box flexGrow={1} sx={{backgroundColor: '#546e7aab'}}>
          <Box display="flex" flexWrap="wrap" alignItems="center" justifyContent="center" sx={{
            '& img': {
              width: '140px',
              margin: '15px'
            }
          }}>
            <Box>
              <a href="https://www.utas.edu.au/">
                <img src="/logos/500x130-UTAS.png" alt="University of Tasmania home page" />
              </a>
            </Box>
            <Box>
              <a href="https://portal.aodn.org.au/">
                <img src="https://static.emii.org.au/images/logo/AODN_logo_fullText.png" alt="Australian Ocean Data Network" />
              </a>
            </Box>
            <Box>
              <a href="https://imos.org.au/">
                <img src="https://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png" alt="Integrated Marine Observation System" />
              </a>
            </Box>
            <Box>
              <a href="https://www.ncris-network.org.au/">
                <img
                    src="https://static.emii.org.au/images/logo/NCRIS_2017_110.png"
                    alt="National Collaborative Research Infrastructure Strategy"
                />
              </a>
            </Box>
            <Box>
              <a href="https://www.nsw.gov.au/">
                <img src="/logos/500x400-waratah-nsw-government.png" alt="NSW Government" />
              </a>
            </Box>
            <Box>
              <a href="https://www.sa.gov.au/">
                <img src="/logos/500x400-South-Australia-white-on-transparent.png" alt="Government of South Australia" />
              </a>
            </Box>
            <Box>
              <a href="https://parks.tas.gov.au/">
                <img src="/logos/500x400-Tasmanian-Parks-white-on-transparent.png" alt="Tasmania Parks and Wildlife Service" />
              </a>
            </Box>
            <Box>
              <a href="https://www.parks.vic.gov.au/">
                <img src="/logos/500x400-Victoria-Parks-white-on-transparent.png" alt="Parks Victoria" />
              </a>
            </Box>
          </Box>
        </Box>
      </>
  );
};

export default Homepage;