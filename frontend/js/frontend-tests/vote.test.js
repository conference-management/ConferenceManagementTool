// import {sum} from './voteTestFile.js';

import puppeteer from 'puppeteer';

// import { startVoteResult } from './voting.js';

// test('adds 1 + 2 to equal 3', () => {
//   expect(sum(1, 2)).toBe(3);
// });

describe('Vote Panel', () => {

  // -----------------------------------------Create Vote----------------------------
  // --------------------------------------------------------------------------------

// test('Create Vote', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);

//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.waitFor(2000);
//   await page.screenshot({path: 'Part-A-CV.png'}); 
//   await page.click('button#createVote');
//   await page.screenshot({path: 'Part-B-CV.png'}); 
//   await page.click('input#VoteText');
//   await page.type('input#VoteText', 'Vote Question');
//   await page.screenshot({path: 'Part-C-CV.png'});
//   await page.click('input#duration');
//   await page.type('input#duration', '2');
//   await page.screenshot({path: 'Part-D-CV.png'});

//   // By default vote is Named vote but here i make it anonymous i.e 0 value
//   await page.$eval('input[type="radio"][value="0"]', radios => {
//     // checkboxes.forEach(chbox => chbox.click())
//     radios.click()
//  });
//  await page.screenshot({path: 'Part-E-CV.png'});
  

//   await page.click('button#confirm');
//   await page.screenshot({path: 'Part-F-CV.png'});
//   await page.waitFor(3000);
//   const confirmCreateVote = await page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => {return "Vote Question"});
//   await page.screenshot({path: 'Part-G-CV.png'});
//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.screenshot({path: 'Part-H-CV.png'});
//   await page.waitFor(2000);
//   // console.log(confirmCreateVote);
//     var startVote;
//     var addVote;
//     var deleteVote;
//     var saveChanges;
//     const [button] = await page.$x("//button[contains(., 'Start Vote')]");
//     const [button1] = await page.$x("//button[contains(., 'Add')]");
//     const [button2] = await page.$x("//button[contains(., 'Save Changes')]");
//     const [button3] = await page.$x("//button[contains(., 'Delete')]");
    
//   if (button) {
//     return startVote = 'Start Vote';
//    }
//   if (button1) {
//     return addVote = 'Add';
//   }
//   if (button2) {
//     return deleteVote = 'Delete';
//   }
//   if (button3) {
//     return saveChanges = 'Save Changes';
//   }
//     expect(startVote).toBe('Start Vote');
//     expect(addVote).toBe('Add');
//     expect(deleteVote).toBe('Delete');
//     expect(saveChanges).toBe('Save Changes');

//   expect(confirmCreateVote).toBe('Vote Question');
//   await page.waitFor(2000);
//   console.log('Vote Created Successfully');
//   // await browser.close();
// });


// ------------------------------create input text field option with button test------------------------------
// -----------------------------------------------------------------------------------------------------------

// test('Create input text field with button test', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.waitFor(3000);

  
//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.waitFor(2000); 
//   await page.screenshot({path: 'Before.png'});
//   const [button] = await page.$x("//button[contains(., 'Add')]");
//   if (button) {
    
//     for(var num = 3; num >= 1; num--){
//       await button.click();
//     }
//   }
   
//   await page.screenshot({path: 'middle.png'});
//     await page.$$eval('input[type=text]', el => {
//       var i = 0;   
//     el.forEach(el1 => {
//       el1.value = 'testing'+i;
//       i++;})
//   });
//   await page.screenshot({path: 'middle1.png'});
//   await page.waitFor(2000); 
//   const [button1] = await page.$x("//button[contains(., 'Save Changes')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.screenshot({path: 'after.png'});
//   console.log("Input fields created successfully!");
  
// });


// ----------------------------------Save Changes for option test----------------------------
// ------------------------------------------------------------------------------------------

// test('Save changes for options Test', async () => {

//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});

//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.waitFor(2000); 
//   await page.screenshot({path: 'Part-A.png'}); 
//   const [button] = await page.$x("//button[contains(., 'Add')]");
//   if (button) {
    
//     for(var num = 3; num >= 1; num--){
//       await button.click();
//     }
//   }
//   await page.screenshot({path: 'Part-B.png'}); 
//     await page.$$eval('input[type=text]', el => {
//       var i = 0;   
//     el.forEach(el1 => {
//       el1.value = 'testing'+i;
//       i++;})
//   });
//   await page.waitFor(3000);
//   await page.screenshot({path: 'Part-C.png'}); 
//   const [button1] = await page.$x("//button[contains(., 'Save Changes')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.screenshot({path: 'Part-D.png'}); 
//   await page.waitFor(3000);
//   console.log('Options saved successfully!');

// });

// ----------------------------Remove Particular vote option---------------------
// ------------------------------------------------------------------------------

// test('Remove vote option', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});

//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.waitFor(2000);
//     await page.screenshot({path: 'Part-A.png'}); 
//     const [button] = await page.$x("//button[contains(., 'Remove')]");
//   if (button) {
//       await button.click();
//   }
//   await page.screenshot({path: 'Part-B.png'});
//   const [button1] = await page.$x("//button[contains(., 'Save Changes')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.screenshot({path: 'Part-C.png'});
//   console.log('Option removed successfuly');

// });

// -------------------------------------------Start Vote Test----------------------------------------
// --------------------------------------------------------------------------------------------------

// test('Start a vote', async () => {

//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.waitFor(2000);
//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.waitFor(2000);
//   await page.screenshot({path: 'Part-A.png'}); 
//   const [button] = await page.$x("//button[contains(., 'Add')]");
//   if (button) {
    
//     for(var num = 3; num >= 1; num--){
//       await button.click();
//     }
//   }
     
//     await page.$$eval('input[type=text]', el => {
//       var i = 0;   
//     el.forEach(el1 => {
//       el1.value = 'testing'+i;
//       i++;})
//   });
//   await page.waitFor(3000);
//   await page.screenshot({path: 'Part-B.png'}); 
//   const [button1] = await page.$x("//button[contains(., 'Start Vote')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.screenshot({path: 'Part-C.png'}); 
//   await page.waitFor(3000);
//   await page.screenshot({path: 'Part-D.png'}); 
//   console.log('Vote has been started!');
// });

// ------------------------------------ Display Active Vote-------------------------- -----------
// ----------------------------------------------------------------------------------------------

// test('Display Active Vote', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.screenshot({path: 'Part-A-D.png'}); 
//   await page.waitFor(3000);
//   let voteQuestion = await page.$eval('span.d-flex', el => el.innerText );
//   console.log(voteQuestion);
//   await page.screenshot({path: 'Part-B-D.png'}); 
//   expect(voteQuestion).toBeDefined();

// });

// --------------------------------------Submit answer for vote qeustion test ------------------------------
// -------------------------------------------------------------------------------------------------

// test('Vote submission', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.waitFor(3000);
//   let voteQuestion = await page.$eval('span.d-flex', el => el.innerText );
//   await page.screenshot({path: 'Part-A-S.png'}); 
//   console.log(voteQuestion);
//   expect(voteQuestion).toBe('Vote Question');
//   // page.waitForXPath("//td[contains(., 'this is question 3')]").then(selector => selector.click());
//   await page.waitFor(2000);

//   await page.$eval('input[type="radio"]', radios => {
//     radios.click()
//  });
//  await page.screenshot({path: 'Part-B-S.png'}); 
//  await page.waitFor(2000);
//   const [button1] = await page.$x("//button[contains(., 'Submit Vote')]");
//   if (button1) {
//       await button1.click();
//   } 
//   await page.waitFor(2000);
//   await page.screenshot({path: 'Part-C-S.png'}); 
//   console.log("Vote submitted successfully!");

// });


// ---------------------------------------------Display Previous Votes---------------------------
// ----------------------------------------------------------------------------------------------

// test('Display Previous Votes', async () => {
//   jest.setTimeout(30000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.waitFor(3000);
//   await page.screenshot({path: 'Part-A.png'});
//   let voteQuestion = await page.$eval('h5.mb-0', el => el.innerText );
  
//   const [button1] = await page.$x("//button[contains(., 'see full details')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.waitFor(3000);
//   await page.screenshot({path: 'Part-B.png'});
//   expect(voteQuestion).toBeDefined();
//   console.log(voteQuestion);
  
// });

// -------------------------------------------------Delete Vote Test--------------------------
// -------------------------------------------------------------------------------------------

// test("Delete Vote Test", async () => {
//   jest.setTimeout(20000);
//   const browser = await puppeteer.launch({
//     // headless: false,
//     // slowMo: 100,
//     // args: ['--window-size=1366,768']
//   })
//   const page = await browser.newPage();
//   await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});

//   await page.click('input#name');
//   await page.type('input#name', 'admin');
//   await page.click('input#password');
//   await page.type('input#password', 'admin');
//   await Promise.all([
//     page.waitForNavigation(), // The promise resolves after navigation has finished
//     page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
//   ]);
//   // await page.waitFor(2000);
//   await page.goto('http://localhost/CMT/vote.html', {waitUntil: 'domcontentloaded'});
//   await page.screenshot({path: 'Part-A-DV.png'}); 
//   page.waitForXPath("//td[contains(., 'Vote Question')]").then(selector => selector.click());
//   await page.waitFor(2000); 
//   await page.screenshot({path: 'Part-B-DV.png'}); 
//   const [button1] = await page.$x("//button[contains(., 'Delete')]");
//   if (button1) {
//       await button1.click();
//   }
//   await page.screenshot({path: 'Part-C-DV.png'}); 
//   await page.waitFor(3000);
//   console.log('Vote has been deleted successfully!');
// });



});
