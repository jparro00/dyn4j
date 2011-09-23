package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.panels.BodyPanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog to create a new body without any fixtures.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EditBodyDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The body config panel */
	private BodyPanel pnlBody;
	
	/** The transform config panel */
	private TransformPanel pnlTransform;
	
	/** The body using in configuration */
	private SandboxBody body;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param body the body to edit
	 */
	private EditBodyDialog(Window owner, String title, SandboxBody body) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		
		this.body = new SandboxBody();
		this.body.setOutlineColor(body.getOutlineColor());
		this.body.setFillColor(body.getFillColor());
		this.body.setActive(body.isActive());
		this.body.setAngularDamping(body.getAngularDamping());
		this.body.setAngularVelocity(body.getAngularVelocity());
		this.body.setAsleep(body.isAsleep());
		this.body.setAutoSleepingEnabled(body.isAutoSleepingEnabled());
		this.body.setBullet(body.isBullet());
		this.body.setGravityScale(body.getGravityScale());
		this.body.setLinearDamping(body.getLinearDamping());
		this.body.setName(body.getName());
		this.body.setVelocity(body.getVelocity().copy());
		this.body.setMassExplicit(body.isMassExplicit());
		
		// copy over the force/torque
		this.body.apply(body.getAccumulatedForce());
		this.body.apply(body.getAccumulatedTorque());
		
		// add the fixtures to the body copy
		// its possible that the mass will be reset here on
		// each fixture add in the future, so set the mass to
		// the mass of the body after this
		int fSize = body.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bf = body.getFixture(i);
			this.body.addFixture(bf);
		}
		
		this.body.setMass(new Mass(body.getMass()));
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		JTabbedPane tabs = new JTabbedPane();
		
		pnlBody = new BodyPanel(this, this.body);
		pnlTransform = new TransformPanel(body.getTransform(), null);
		
		tabs.addTab("Body", this.pnlBody);
		tabs.addTab("Transform", this.pnlTransform);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnSave = new JButton("Save");
		btnSave.setActionCommand("save");
		btnCancel.setActionCommand("cancel");
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabs)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnSave))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabs)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnSave)));
		
		this.pack();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// check the action command
		if ("cancel".equals(event.getActionCommand())) {
			// if its canceled then set the canceled flag and
			// close the dialog
			this.setVisible(false);
			this.canceled = true;
		} else {
			// check the body input
			if (this.pnlBody.isValidInput()) {
				// check the transform input
				if (this.pnlTransform.isValidInput()) {
					// if its valid then close the dialog
					this.canceled = false;
					this.setVisible(false);
				} else {
					this.pnlTransform.showInvalidInputMessage(this);
				}
			} else {
				this.pnlBody.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows an Edit Body Dialog using the values in the current body.
	 * @param owner the dialog owner
	 * @param title the dialog title
	 * @param body the body to edit
	 */
	public static final void show(Window owner, String title, SandboxBody body) {
		EditBodyDialog dialog = new EditBodyDialog(owner, title, body);
		dialog.setLocationRelativeTo(owner);
		dialog.setIconImage(Icons.EDIT_BODY.getImage());
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the body and fixture
			SandboxBody bodyChanges = dialog.body;
			
			body.setOutlineColor(bodyChanges.getOutlineColor());
			body.setFillColor(bodyChanges.getFillColor());
			body.setActive(bodyChanges.isActive());
			body.setAngularDamping(bodyChanges.getAngularDamping());
			body.setAngularVelocity(bodyChanges.getAngularVelocity());
			body.setAsleep(bodyChanges.isAsleep());
			body.setAutoSleepingEnabled(bodyChanges.isAutoSleepingEnabled());
			body.setBullet(bodyChanges.isBullet());
			body.setGravityScale(bodyChanges.getGravityScale());
			body.setLinearDamping(bodyChanges.getLinearDamping());
			body.setMass(new Mass(bodyChanges.getMass()));
			body.setName(bodyChanges.getName());
			body.setVelocity(bodyChanges.getVelocity().copy());
			body.setMassExplicit(bodyChanges.isMassExplicit());
			
			// apply the transform
			body.getTransform().identity();
			body.translate(dialog.pnlTransform.getTranslation());
			body.rotateAboutCenter(dialog.pnlTransform.getRotation());
		}
	}
}
