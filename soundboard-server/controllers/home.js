/**
 * GET /
 * Home page.
 */
exports.index = (req, res) => {
     res.redirect('/login')
  // res.render('home', {
  //   title: 'Home'
  // });
};

exports.getEventually = (req, res) => {
  res.render('html-up-eventually', {
    title: 'Eventually'
  });
};
