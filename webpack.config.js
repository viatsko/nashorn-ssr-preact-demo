const path = require('path');

module.exports = {
    entry: {
        preact: path.join(__dirname, 'src/main/js/index.js')
    },
    output: {
        path: path.join(__dirname, 'src/main/webapp/bundles'),
        filename: '[name].bundle.js'
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loaders: ['babel-loader']
            }
        ]
    }
};
