import React from 'react';
import {Typography} from '@material-ui/core';
import createMuiTheme from '@material-ui/core/styles/createMuiTheme';
import ThemeProvider from '@material-ui/styles/ThemeProvider';
import responsiveFontSizes from '@material-ui/core/styles/responsiveFontSizes';
import Grid from '@material-ui/core/Grid';
import makeStyles from '@material-ui/core/styles/makeStyles';
import Box from '@material-ui/core/Box';

const maxContentWidth = 700;

let theme = createMuiTheme({
  typography: {
    fontFamily: [
      'Lora'
    ].join(','),
    h1: {
      fontSize: '4.5rem',
      '@media (min-width:900px)': {
        fontSize: '4rem',
      }
    },
    h3: { fontSize: '1.8rem'},
    h2: { fontSize: '2.4rem'},
    h4: { fontSize: '1.5rem'}
  }
});
theme = responsiveFontSizes(theme);

const useStyles = makeStyles(() => ({
  spacer: {
    '& > *': {
      marginTop: 20,
      marginBottom: 20,
      padding: 10
    }
  },
  boxes: {
    marginTop: 10,
    marginBottom: 20,
    maxWidth: 700
  },
  supporters: {
    backgroundColor: '#546e7aab'
  },
  grey: { color: '#999'},
  white: { color: '#FFF'}
}));

const logoData = [
  {
    img: '/logos/500x130-UTAS.png',
    href: 'http://www.utas.edu.au/',
    title: 'University of Tasmania home page'
  },
  {
    img: 'https://static.emii.org.au/images/logo/AODN_logo_fullText.png',
    href: 'https://portal.aodn.org.au/',
    title: 'Australian Ocean Data Network'
  },
  {
    img: 'https://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png',
    href: 'https://imos.org.au/',
    title: 'Integrated Marine Observation System'
  },
  {
    img: 'https://static.emii.org.au/images/logo/NCRIS_2017_110.png',
    title: 'National Collaborative Research Infrastructure Strategy',
    href: 'https://www.ncris-network.org.au/'
  },
  {
    img: '/logos/500x400-waratah-nsw-government.png',
    href: 'https://www.nsw.gov.au/',
    title: 'NSW Government'
  },
  {
    img: '/logos/500x400-South-Australia-white-on-transparent.png',
    href: 'https://www.sa.gov.au/',
    title: 'Government of South Australia'
  },
  {
    img: '/logos/500x400-Tasmanian-Parks-white-on-transparent.png',
    href: 'https://parks.tas.gov.au/',
    title: 'Tasmania Parks and Wildlife Service'
  },
  {
    img: '/logos/500x400-Victoria-Parks-white-on-transparent.png',
    href: 'https://www.parks.vic.gov.au/',
    title: 'Parks Victoria'
  }
];

const Homepage = () => {

  const classes = useStyles();

  return <>

  <ThemeProvider theme={theme}>
    <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
        className={classes.spacer}
    >
      <Grid item>
        <Grid
            container
            alignItems="flex-start"
            justify="space-around"
            direction="column"
            className={classes.spacer}
        >
          <Grid item>

            <Typography variant={'h1'}>NRMN - Data Portal</Typography>
            <Typography variant={'h3'} className={classes.grey}>Underwater visual census data on shallow reefs</Typography>
          </Grid>
          <Grid item>
            <Box className={classes.boxes}>
              <Typography variant={'h4'}>NRMN is an IMOS sub-facility. Its roles:</Typography>
              <ul>
                <li>Collate, clean, store and make rapidly available, all data obtained
                  during shallow reef surveys conducted by the National Reef
                  Monitoring Network
                </li>
                <li>Foster formal links with external monitoring programs for integrated use and
                  outcomes
                </li>
                <li>Make consistent data available at the national (and
                  international) scale, for shallow reefs
                </li>
              </ul>
              <p>
                <b>Australia’s Integrated Marine Observing System (IMOS)</b> is enabled by the National Collaborative
                Research Infrastructure Strategy (NCRIS).
                It is operated by a consortium of institutions as an unincorporated joint venture, with the
                <a title="UTAS home page"
                   href="http://www.utas.edu.au/"> University
                  of Tasmania</a> as Lead Agent.
              </p>
            </Box>

          </Grid>
          <Grid item>

            <Box className={classes.boxes}>


              <Typography variant={'h4'}>
                Public data from the NRMN is available via the <a title="AODN Portal"  rel="noreferrer"  target="_blank" href="https://portal.aodn.org.au/" >AODN Portal</a>.
              </Typography>

              <p>
                <a title="Email us for help in using this site"
                      href="mailto:info@aodn.org.au?subject=Portal enquiry - ">Contact</a>
                <b> | </b>
                <a  title="Data usage acknowledgement"
                      href="https://help.aodn.org.au/user-guide-introduction/aodn-portal/data-use-acknowledgement">Acknowledgement</a>
                <b> | </b>
                <a  title="Disclaimer information"
                      href="https://help.aodn.org.au/user-guide-introduction/aodn-portal/disclaimer">Disclaimer</a>
                <b> | </b>

                <a title="Australian Ocean Data Network" rel="noreferrer"  target="_blank"
                      href="http://imos.org.au/aodn.html">AODN</a>
                <b> | </b>
                <a title="Integrated Marine Observing System"
                      href="http://www.imos.org.au/">IMOS</a>
              </p>
            </Box>
          </Grid>
        </Grid>
      </Grid>
    </Grid>

    <Box className={classes.supporters}>
      <Grid
          container
          direction="row"
          justify="center"
          alignItems="center"
          className={classes.spacer}
      >
        <Grid item>
          <Box className={classes.spacer}>
            <span>
              <Typography className={classes.white} variant={'h1'} >Our Partners</Typography>
              <Typography className={classes.white} > We acknowledge the generous support of our partners</Typography>
            </span>
          </Box>
          <Box
              display="flex"
              flexWrap="wrap"
              css={{maxWidth: maxContentWidth}}
              className={classes.spacer}
          >

            {logoData.map((tile) => (
                <Box pr={2}
                     pb={2}
                     mr={1}
                  width={150}
                  key={tile.title + '-logo'}
                >
                  <a href={tile.href} ><img
                      width={140}
                      src={process.env.PUBLIC_URL + tile.img}
                      alt={tile.title}
                  /></a>
                </Box>
            ))}
          </Box>
        </Grid>
      </Grid>
    </Box>
  </ThemeProvider>
</>;

};

export default Homepage;