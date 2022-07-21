package com.example.dash;

import java.util.ArrayList;
import java.util.Date;

public class account extends Customer{
    Date CreatedOn, LastActiveOn;
    ArrayList<AccountEntry> Ledger;

    public boolean createAccount(Customer customer){

        return true;
    }
    public Date getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(Date createdOn) {
        CreatedOn = createdOn;
    }

    public Date getLastActiveOn() {
        return LastActiveOn;
    }

    public void setLastActiveOn(Date lastActiveOn) {
        LastActiveOn = lastActiveOn;
    }

    public ArrayList<AccountEntry> getLedger() {
        return Ledger;
    }

    public void setLedger(ArrayList<AccountEntry> ledger) {
        Ledger = ledger;
    }

    public void addLedgerEntry(String narration, Date EntryDate, Double EntryValue, String EntryType){
        AccountEntry accountEntry = new AccountEntry();
        accountEntry.setNarration(narration);
        accountEntry.setEntryDate(EntryDate);
        accountEntry.setEntryValue(EntryValue);
        accountEntry.setEntryType(EntryType);
        Ledger.add(accountEntry);
    }

    public void updateLedgerEntry(int index, AccountEntry accountEntry){
        Ledger.remove(index);
        Ledger.add(index,accountEntry);
    }

    private static class AccountEntry {
        String narration;
        Date EntryDate;
        Double EntryValue;
        String EntryType;

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        public Date getEntryDate() {
            return EntryDate;
        }

        public void setEntryDate(Date entryDate) {
            EntryDate = entryDate;
        }

        public Double getEntryValue() {
            return EntryValue;
        }

        public void setEntryValue(Double entryValue) {
            EntryValue = entryValue;
        }

        public String getEntryType() {
            return EntryType;
        }

        public void setEntryType(String entryType) {
            EntryType = entryType;
        }
    }
}
