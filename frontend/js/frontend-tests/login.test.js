
import puppeteer from 'puppeteer';


describe('Login Page Validation', () => {

    // test('Invalid Username or Password', async () => {

    //     jest.setTimeout(30000);
    //     const browser = await puppeteer.launch({
    //         // headless: false,
    //         // slowMo: 100,
    //         // args: ['--window-size=1366,768']
    //     })
    //     const page = await browser.newPage();
    //     await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});
    //     await page.screenshot({path: 'Part-A-LI.png'}); 
    //     await page.click('input#name');
    //     await page.type('input#name', 'admin');
    //     await page.click('input#password');
    //     await page.type('input#password', 'admin123');
    //     await page.click('button#submit-button');
    //     await page.waitFor(3000);
    //     await page.screenshot({path: 'Part-B-LI.png'}); 
    //     let validationMessage = await page.$eval('span#message', el => el.innerText);

    //     await page.screenshot({path: 'Part-C-LI.png'}); 
    //     expect(validationMessage).toBe('Incorrect password');
    //     console.log(validationMessage);
        

    // });

    test('Logged in successfully', async () => {

        jest.setTimeout(30000);
        const browser = await puppeteer.launch({
            // headless: false,
            // slowMo: 100,
            // args: ['--window-size=1366,768']
        })
        const page = await browser.newPage();
        await page.goto('http://localhost/CMT/', {waitUntil: 'domcontentloaded'});
        await page.screenshot({path: 'Part-A-LS.png'}); 
        await page.click('input#name');
        await page.type('input#name', 'admin');
        await page.click('input#password');
        await page.type('input#password', 'admin');
        await page.screenshot({path: 'Part-B-LS.png'}); 
        await Promise.all([
            page.waitForNavigation(), // The promise resolves after navigation has finished
            page.click('button#submit-button'), // Clicking the link will indirectly cause a navigation
        ]);
        await page.waitFor(3000);
        await page.screenshot({path: 'Part-C-LS.png'}); 
        console.log('Logged in successfully!');

        });

});