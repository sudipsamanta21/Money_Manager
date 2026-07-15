package com.Sudip.Money_Manager.service;

import com.Sudip.Money_Manager.dataTransferObject.ExpenseDTO;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;


//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")

      @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
        public void sendDailyIncomeExpenseReminder(){
            log.info("Job started: sendDailyIncomeExpenseReminder()");
            List<ProfileEntity> profileEntity = profileRepository.findAll();
            for (ProfileEntity profile : profileEntity) {
                String body =
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
    body {
        margin:0;
        padding:0;
        background:#eef1f6;
        font-family:'Segue UI', Roboto, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing:antialiased;
    }
    .wrapper {
        width:100%%;
        padding:32px 12px;
        box-sizing:border-box;
    }
    .container {
        max-width:600px;
        margin:0 auto;
        background:#ffffff;
        border-radius:20px;
        overflow:hidden;
        box-shadow:0 12px 32px rgba(20,30,60,0.10);
    }
    .header {
        position:relative;
        background:linear-gradient(135deg, #6a11cb 0%%, #2575fc 100%%);
        color:#ffffff;
        text-align:center;
        padding:44px 20px 40px;
    }
    .header::after {
        content:"";
        position:absolute;
        left:0; right:0; bottom:-1px;
        height:24px;
        background:#ffffff;
        border-radius:24px 24px 0 0;
    }
    .header .icon {
        font-size:38px;
        margin-bottom:6px;
        filter:drop-shadow(0 2px 4px rgba(0,0,0,0.15));
    }
    .header .title {
        font-size:24px;
        font-weight:700;
        letter-spacing:0.3px;
    }
    .header .subtitle {
        font-size:13px;
        opacity:0.85;
        margin-top:6px;
        letter-spacing:0.4px;
        text-transform:uppercase;
    }
    .content {
        padding:8px 34px 32px;
        color:#3c3c3c;
        line-height:1.7;
        font-size:15.5px;
    }
    .content h2 {
        margin-top:0;
        font-size:20px;
        color:#1f2430;
    }
    .content p {
        margin:0 0 14px;
        color:#565d6d;
    }
    .stats-row {
        display:flex;
        gap:12px;
        margin:26px 0;
    }
    .stat-card {
        flex:1;
        background:#f8faff;
        border:1px solid #e8ecf3;
        border-radius:12px;
        padding:18px 14px;
        text-align:center;
    }
    .stat-card .emoji { font-size:20px; margin-bottom:6px; }
    .stat-card .label {
        font-size:12.5px;
        color:#8a90a2;
        margin-bottom:4px;
        text-transform:uppercase;
        letter-spacing:0.3px;
    }
    .stat-card .value.income { color:#1E8E3E; font-weight:700; font-size:15px; }
    .stat-card .value.expense { color:#D93025; font-weight:700; font-size:15px; }
    ul.benefits {
        list-style:none;
        padding:0;
        margin:22px 0;
        background:#fbfbfe;
        border-radius:12px;
        border:1px solid #f0f1f6;
    }
    ul.benefits li {
        padding:12px 16px;
        font-size:14.5px;
        border-bottom:1px solid #f0f1f6;
        color:#454b58;
    }
    ul.benefits li:last-child {
        border-bottom:none;
    }
    .cta-wrap {
        text-align:center;
        margin:30px 0 6px;
    }
    .button {
        display:inline-block;
        background:linear-gradient(135deg, #2575fc, #6a11cb);
        color:#ffffff !important;
        text-decoration:none;
        padding:15px 36px;
        border-radius:30px;
        font-weight:600;
        font-size:15px;
        box-shadow:0 8px 20px rgba(37,117,252,0.32);
    }
    .divider {
        height:1px;
        background:#f0f1f6;
        margin:28px 0 18px;
        border:none;
    }
    .footer {
        background:#f8f9fc;
        padding:24px 20px;
        text-align:center;
        font-size:12.5px;
        color:#9aa0b0;
        border-top:1px solid #eef0f5;
    }
    .footer .brand {
        font-weight:700;
        color:#565d6d;
        margin-bottom:4px;
        font-size:13.5px;
    }
    @media only screen and (max-width:480px) {
        .content { padding:8px 22px 26px; }
        .stats-row { flex-direction:column; }
    }
</style>
</head>
<body>
<div class="wrapper">
    <div class="container">

        <div class="header">
            <div class="icon">💰</div>
            <div class="title">Money Manager</div>
            <div class="subtitle">Daily Check-in</div>
        </div>

        <div class="content">
            <h2>Hello, %s 👋</h2>
            <p>We hope you're having a wonderful day!</p>
            <p>
                This is your <b>daily reminder</b> to log today's transactions
                and keep your finances on track.
            </p>

            <div class="stats-row">
                <div class="stat-card">
                    <div class="emoji">📈</div>
                    <div class="label">Income</div>
                    <div class="value income">Add today</div>
                </div>
                <div class="stat-card">
                    <div class="emoji">📉</div>
                    <div class="label">Expense</div>
                    <div class="value expense">Add today</div>
                </div>
            </div>

            <ul class="benefits">
                <li>📊 Track your spending patterns</li>
                <li>💰 Manage your monthly budget</li>
                <li>🎯 Stay on course for your financial goals</li>
            </ul>

            <div class="cta-wrap">
                <a href="%s" class="button">🚀 Open Money Manager</a>
            </div>

            <hr class="divider">

            <p style="margin:0;">
                Thank you for using <b>Money Manager</b>. Have a great day! 😊
            </p>
        </div>

        <div class="footer">
            <div class="brand">Money Manager</div>
            Smart Finance • Better Future <br>
            © 2026 Money Manager Team
        </div>

                 </div>
                </div>
                </body>
                </html>
                 """.formatted(profile.getFullName(), frontendUrl);

                emailService.sendEmail(
                        profile.getEmail(),
                        "Daily Income & Expense Reminder",
                        body
                );
            }
            log.info("Job completed: sendDailyIncomeExpenseReminder()");
        }





    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummary() {

        log.info("Job started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {

            List<ExpenseDTO> todayExpenses =
                    expenseService.findByProfileEntity_IdAndDateWithCategory(
                            profile.getId(),
                            LocalDate.now()
                    );


            if (!todayExpenses.isEmpty()) {

                StringBuilder rows = new StringBuilder();
                BigDecimal total = BigDecimal.ZERO;
                int i = 1;

                for (ExpenseDTO expense : todayExpenses) {
                    total = total.add(expense.getAmount());
                    rows.append("""
                    <tr>
                        <td class="index-cell">%d</td>
                        <td><span class="category-pill">%s</span></td>
                        <td>%s</td>
                        <td class="amount-cell">₹%s</td>
                    </tr>
                    """.formatted(i++, expense.getCategoryName(), expense.getName(), expense.getAmount()));
                }

                String body = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin:0;
                        padding:0;
                        background:#eef1f6;
                        font-family:'Segue UI', Roboto, Helvetica, Arial, sans-serif;
                        -webkit-font-smoothing:antialiased;
                    }
                    .wrapper {
                        width:100%%;
                        padding:32px 12px;
                        box-sizing:border-box;
                    }
                    .container {
                        max-width:600px;
                        margin:0 auto;
                        background:#ffffff;
                        border-radius:20px;
                        overflow:hidden;
                        box-shadow:0 12px 32px rgba(20,30,60,0.10);
                    }
                    .header {
                        position:relative;
                        background:linear-gradient(135deg, #ff5f6d 0%%, #c0392b 100%%);
                        color:#ffffff;
                        text-align:center;
                        padding:40px 20px 36px;
                    }
                    .header::after {
                        content:"";
                        position:absolute;
                        left:0; right:0; bottom:-1px;
                        height:24px;
                        background:#ffffff;
                        border-radius:24px 24px 0 0;
                    }
                    .header .icon { font-size:34px; margin-bottom:6px; }
                    .header .title { font-size:22px; font-weight:700; letter-spacing:0.3px; }
                    .header .subtitle {
                        font-size:13px;
                        opacity:0.9;
                        margin-top:6px;
                        text-transform:uppercase;
                        letter-spacing:0.4px;
                    }
                    .content {
                        padding:8px 30px 30px;
                        color:#3c3c3c;
                        font-size:15px;
                        line-height:1.7;
                    }
                    .content p { margin:0 0 10px; color:#565d6d; }
                    .content .greeting { font-size:17px; color:#1f2430; font-weight:600; margin-bottom:6px; }
                    .total-banner {
                        margin:20px 0 22px;
                        background:#fff5f5;
                        border:1px solid #ffe1e0;
                        border-radius:12px;
                        padding:16px 18px;
                        display:flex;
                        justify-content:space-between;
                        align-items:center;
                    }
                    .total-banner .label {
                        font-size:13px;
                        color:#8a90a2;
                        text-transform:uppercase;
                        letter-spacing:0.3px;
                    }
                    .total-banner .amount {
                        font-size:20px;
                        font-weight:700;
                        color:#D93025;
                    }
                    table.expense-table {
                        width:100%%;
                        border-collapse:collapse;
                        margin:10px 0 8px;
                        border-radius:10px;
                        overflow:hidden;
                        font-size:14px;
                    }
                    table.expense-table thread th {
                        background:#f8faff;
                        color:#565d6d;
                        font-weight:600;
                        text-align:left;
                        padding:12px 10px;
                        border-bottom:2px solid #eef0f5;
                        font-size:12.5px;
                        text-transform:uppercase;
                        letter-spacing:0.3px;
                    }
                    table.expense-table body td {
                        padding:12px 10px;
                        border-bottom:1px solid #f0f1f6;
                        color:#3c3c3c;
                    }
                    table.expense-table body tr:last-child td {
                        border-bottom:none;
                    }
                    table.expense-table body tr:nth-child(even) {
                        background:#fbfbfe;
                    }
                    table.expense-table td.amount-cell {
                        font-weight:600;
                        color:#D93025;
                        text-align:right;
                    }
                    table.expense-table td.index-cell {
                        color:#9aa0b0;
                        width:28px;
                    }
                    .category-pill {
                        display:inline-block;
                        background:#eef2ff;
                        color:#3949ab;
                        font-size:12px;
                        font-weight:600;
                        padding:3px 10px;
                        border-radius:20px;
                    }
                    .divider { height:1px; background:#f0f1f6; margin:26px 0 16px; border:none; }
                    .footer {
                        background:#f8f9fc;
                        padding:24px 20px;
                        text-align:center;
                        font-size:12.5px;
                        color:#9aa0b0;
                        border-top:1px solid #eef0f5;
                    }
                    .footer .brand { font-weight:700; color:#565d6d; margin-bottom:4px; font-size:13.5px; }

                    @media only screen and (max-width:480px) {
                        .content { padding:8px 18px 24px; }
                        table.expense-table { font-size:12.5px; }
                        table.expense-table thread th,
                        table.expense-table body td { padding:8px 6px; }
                    }
                </style>
                </head>
                <body>
                <div class="wrapper">
                    <div class="container">
                        <div class="header">
                            <div class="icon">🧾</div>
                            <div class="title">Money Manager</div>
                            <div class="subtitle">Today's Expense Summary</div>
                        </div>
                        <div class="content">
                            <div class="greeting">Hi %s,</div>
                            <p>Here's a quick look at everything you spent today.</p>
                            <div class="total-banner">
                                <div class="label">Total Spent Today</div>
                                <div class="amount">₹%s</div>
                            </div>
                            <table class="expense-table">
                                <thread>
                                    <tr>
                                        <th>#</th>
                                        <th>Category</th>
                                        <th>Expense</th>
                                        <th style="text-align:right;">Amount</th>
                                    </tr>
                                </thread>
                                <body>%s</body>
                            </table>
                            <hr class="divider">
                            <p style="margin:0;">Regards,<br><b>Money Manager Team</b></p>
                        </div>
                        <div class="footer">
                            <div class="brand">Money Manager</div>
                            Smart Finance • Better Future <br>© 2026 Money Manager Team
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(profile.getFullName(), total.setScale(2, java.math.RoundingMode.HALF_UP), rows.toString());

                emailService.sendEmail(
                        profile.getEmail(),
                        "Your Daily Expense Summary",
                        body
                );
            }
        }
        log.info("Job completed: sendDailyExpenseSummary()");
    }


}
