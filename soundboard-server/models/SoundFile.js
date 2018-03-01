const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const soundFileSchema = new mongoose.Schema({
     owner: { type: Schema.Types.ObjectId, ref: 'User' },
     originalname: { type: String },
     filename: { type: String }
}, { timestamps: true });

const SoundFile = mongoose.model('SoundFile', soundFileSchema);

module.exports = SoundFile;
