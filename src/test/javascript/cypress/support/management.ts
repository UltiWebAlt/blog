Cypress.Commands.add('getManagementInfo', () =>
  cy
    .request({
      method: 'GET',
      url: '/management/info',
    })
    .then(response => cy.wrap(response.body)),
);

declare global {
  namespace Cypress {
    interface Chainable {
      getManagementInfo(): Cypress.Chainable;
    }
  }
}

// Convert this to a module instead of script (allows import/export)
export {};
