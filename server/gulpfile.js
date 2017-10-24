var gulp = require('gulp');
var babel = require('gulp-babel');
var json = require('gulp-json-editor');
var deleteLines = require('gulp-delete-lines');

// Compile babel and move files to build directory
gulp.task('default', function () {
  // Setup scripts for GAE deployment
  gulp.src('package.json').pipe(json((json) => {
    delete json.scripts.build;
    delete json.scripts.clean;
    delete json.scripts.lint;
    delete json.devDependencies;
    json.scripts.deploy = 'gcloud app deploy';
    json.scripts.start  = 'node index.js';
    return json;
  })).pipe(gulp.dest('build'));

  // disable GraphiQL
  gulp.src('src/**/*.js').pipe(deleteLines({
    filters: [ /.+\/graphiql.+/ ]
  })).pipe(babel()).pipe(gulp.dest('build'));

  gulp.src('app.yaml').pipe(gulp.dest('build'));
  gulp.src('src/**/*.json').pipe(gulp.dest('build'));
  gulp.src('src/**/*.gql').pipe(gulp.dest('build'));
});