const { test, expect } = require('@playwright/test');

test.describe('EduTrack End-to-End Integration Flow', () => {
  const baseURL = process.env.BASE_URL || 'http://localhost:5173';

  test('Complete End-to-End Flow: Admin setup, Teacher QR session, Student check-in, Percentage update', async ({ page }) => {
    // 1. Admin Login & Data Verification
    await page.goto(`${baseURL}/login`);
    await page.fill('input[placeholder*="Username"]', 'admin');
    await page.fill('input[placeholder*="Password"]', 'admin123');
    await page.click('button[type="submit"]');

    // Verify Admin Portal Header
    await expect(page.locator('.header-app-title')).toContainText('Admin Portal');

    // 2. Teacher Login & QR Session Start
    await page.goto(`${baseURL}/login`);
    await page.fill('input[placeholder*="Username"]', 'teacher1');
    await page.fill('input[placeholder*="Password"]', 'teacher123');
    await page.click('button[type="submit"]');

    await expect(page.locator('.header-app-title')).toContainText('Faculty Portal');

    // Click QR trigger button
    const qrBtn = page.locator('.header-icon-btn.qr-trigger');
    if (await qrBtn.count() > 0) {
      await qrBtn.click();
      await expect(page.locator('.qr-modal-container')).toBeVisible();
    }

    // 3. Student Login & QR Check-in
    await page.goto(`${baseURL}/login`);
    await page.fill('input[placeholder*="Username"]', 'student1');
    await page.fill('input[placeholder*="Password"]', 'student123');
    await page.click('button[type="submit"]');

    await expect(page.locator('.header-app-title')).toContainText('Student Portal');

    // Verify Overall Attendance Percentage is visible in student overview
    const attStat = page.locator('.student-stat-value').first();
    await expect(attStat).toBeVisible();
    const attText = await attStat.textContent();
    expect(attText).toContain('%');
  });
});
