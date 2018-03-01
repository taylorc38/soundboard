// Models
const User = require('../models/User');
const SoundFile = require('../models/SoundFile');
const fs = require('fs');
const passport = require('passport');
const jwt = require('jsonwebtoken');
const _ = require('lodash');
const config = require('../config/main');

/**
* GET /api/upload
* File Upload API example.
*/

exports.login = (req, res) => {
     if (!req.body.email) {
          return res.status(422).json({ msg: "Please provide an email" })
     }

     if (!req.body.password) {
          return res.status(422).json({ msg: "Please provide a password" })
     }

     User.findOne({ 'email': req.body.email }, (err, user) => {
          if (err) {
               return res.status(422).json({ msg: "Could not find user with that email - " + err })
          }

          user.comparePassword(req.body.password, (err, isMatch) => {
               if (err) {
                    return res.status(422).json({ msg: err })
               }

               if (isMatch) {
                    let payload = { id: user._id }
                    let token = "JWT " + jwt.sign(payload, config.secret)

                    return res.status(200).json({ token: token })
               } else {
                    return res.status(422).json({ msg: "Password did not match" })
               }
          });
     });
};

exports.getSoundboard = (req, res) => {
     res.render('api/soundboard', {
          title: 'Soundboard'
     });
}

exports.getFileUpload = (req, res) => {
     res.render('api/upload', {
          title: 'File Upload'
     });
};

exports.postFileUpload = (req, res) => {
     // We will need the originalname and filename later, so store them under this user
     User.findById(req.user._id, (err, user) => {
          if (err) {
               req.flash('errors', { msg: 'Oops! User not found.' });
               res.redirect('/api/soundboard');
          }

          const soundFile = new SoundFile({
               owner: req.user._id,
               originalname: req.file.originalname,
               filename: req.file.filename
          });

          soundFile.save((err, saved) => {
               if (err) {
                    req.flash('errors', { msg: 'Oops! User not found.' });
                    res.redirect('/api/soundboard');
               }

               req.flash('success', { msg: 'File was uploaded successfully.' });
               res.redirect('/soundboard');
          });
     });
};

exports.getUserDownloads = (req, res) => {
     SoundFile.find({ 'owner': req.user._id })
     .select('filename originalname -_id')
     .exec((err, soundFiles) => {
          if (err) {
               return res.status(422).json({ msg: 'Error looking up files' })
          }

          return res.status(200).json({ files: soundFiles });
     });
};

// url param: fileId
exports.downloadFile = (req, res) => {
     SoundFile.findOne({ 'filename': req.params.filename }, (err, soundFile) => {
          if (err) {
               return res.status(422).json({ msg: 'Error looking up file' })
          }

          let path = 'uploads/' + req.params.filename
          fs.readFile(path, (err, data) => {
               if (err) throw err

               // Store binary in a buffer
               let buffer = Buffer.from(data, 'binary')

               // Delete the file from the server and remove it from this User's sound files
               fs.unlink(path)
               soundFile.remove()

               // Send the binary in response
               res.write(buffer, 'binary')
               res.end(null, 'binary')
          });
     });
};

exports.testDownload = (req, res) => {
     fs.readFile('uploads/test.wav', (err, data) => {
          if (err) throw err

          var buffer = Buffer.from(data, 'binary')
          res.write(buffer, 'binary')
          res.end(null, 'binary')
     })
}
