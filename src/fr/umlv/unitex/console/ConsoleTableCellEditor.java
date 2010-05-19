/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.console;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import fr.umlv.unitex.frames.ConsoleFrame;

@SuppressWarnings("serial")
public class ConsoleTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JButton button;
    private int currentRow=-1;
    ConsoleTableModel model;
    
    public ConsoleTableCellEditor(final ConsoleTableModel model) {
        this.model=model;
        button=new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        button.addActionListener(new ActionListener() {
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(ActionEvent e) {
                if (button.getIcon()==ConsoleFrame.statusErrorDown) {
                    /* If we want to show an error message */
                    ConsoleEntry entry=model.getConsoleEntry(currentRow);
                    Console__.addCommand(entry.getErrorMessage(),false,currentRow+1,false);
                    entry.setStatus(2);
                } else {
                    ConsoleEntry entry=model.getConsoleEntry(currentRow);
                    /* We reset the error button of the father to the down position */
                    entry.setStatus(1);
                    model.removeEntry(currentRow+1);
                }
            fireEditingStopped();
        }});
    }

    public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row, int column) {
        Integer i=(Integer)value;
        if (i==0 || i==3) {
            currentRow=-1;
            return null;
        }
        if (i==1) {
            button.setIcon(ConsoleFrame.statusErrorDown);
        }
        if (i==2) {
            button.setIcon(ConsoleFrame.statusErrorUp);
        }
        currentRow=row;
        return button;
    }

    public Object getCellEditorValue() {
        return null;
    }

}
