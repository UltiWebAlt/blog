// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)
import { existsSync, mkdirSync, writeFileSync } from 'fs';
import { lighthouse, pa11y, prepareAudit } from 'cypress-audit';
import webpackPreprocessor from '@cypress/webpack-preprocessor';

export default async (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions): Promise<Cypress.PluginConfigOptions> => {
  // Use the project's TS6-compatible loader instead of Cypress 13's bundled loader.
  on(
    'file:preprocessor',
    webpackPreprocessor({
      webpackOptions: {
        resolve: { extensions: ['.ts', '.js', '.json'] },
        module: {
          rules: [
            {
              test: /\.ts$/,
              exclude: /node_modules/,
              use: [{ loader: 'ts-loader', options: { transpileOnly: true } }],
            },
          ],
        },
      },
    }),
  );
  on('before:browser:launch', (browser, launchOptions) => {
    prepareAudit(launchOptions);
    if (browser.name === 'chrome' && browser.isHeadless) {
      launchOptions.args.push('--disable-gpu');
      return launchOptions;
    }
  });

  // Allows logging with cy.task('log', 'message') or cy.task('table', object)
  on('task', {
    log(message) {
      // eslint-disable-next-line no-console
      console.log(message);
      return null;
    },
    table(message) {
      // eslint-disable-next-line no-console
      console.table(message);
      return null;
    },
  });

  on('task', {
    lighthouse: lighthouse(async lighthouseReport => {
      const { default: ReportGenerator } = await import('lighthouse/report/generator/report-generator');
      if (!existsSync('target/cypress/')) {
        mkdirSync('target/cypress/', { recursive: true });
      }
      writeFileSync('target/cypress/lhreport.html', ReportGenerator.generateReport(lighthouseReport.lhr, 'html'));
    }),
    pa11y: pa11y(),
  });
  (await import('@cypress/code-coverage/task')).default(on, config);
  return config;
};
