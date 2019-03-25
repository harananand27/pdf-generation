package payroll;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jnlp.FileContents;

@RestController
class EmployeeController {

	private final EmployeeRepository repository;

	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	// Aggregate root

	@GetMapping("/employees")
	List<Employee> all() {
		return repository.findAll();
	}


	@PostMapping("/generate-pdf")
	void returnPDf(@RequestBody Map<String, String> fields) throws IOException {
		File file = new File("form.pdf");
		PDDocument document = PDDocument.load(file);
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

		PDFont font = PDType1Font.HELVETICA;
		PDResources resources = new PDResources();
		resources.put(COSName.getPDFName("Helv"), font);
		acroForm.setDefaultResources(resources);
		String defaultAppearanceString = "/Helv 0 Tf 0 g";
		acroForm.setDefaultAppearance(defaultAppearanceString);
		Set<String> fieldNames = fields.keySet();
		defaultAppearanceString = "/Helv 12 Tf 0 0 0 rg";
		// Add and set the resources and default appearance at the form level
		for (String fieldName : fieldNames) {
			PDTextField field = (PDTextField) acroForm.getField(fieldName);
			field.setDefaultAppearance(defaultAppearanceString);
			field.setValue(fields.get(fieldName));
		}
		List<PDField> yo = acroForm.getFields();
		for (PDField pdField : yo) {
			System.out.println(pdField.getFullyQualifiedName());
		}
		//Saving the document


		document.save("my_doc.pdf");

		System.out.println("PDF created");

		//Closing the document
		document.close();
	}

}
