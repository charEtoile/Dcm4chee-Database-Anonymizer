# parseDicomBlob
little project to understand dicom blobs in dcm4chee v2 database

Tags to anonymise and retrieved from DCM4CHEE and found in the configuration files:

https://dcm4che.atlassian.net/wiki/display/ee2/DICOM+Attribute+Filter
https://groups.google.com/forum/#!topic/dcm4che/zkT4O7ZOY6c


    <dcm4chee-attribute-filter>
        <patient>
            <attr tag="00100010" case-sensitive="false"/> <!-- Patient's Name -->
            <attr tag="00100030"/> <!-- Patient's Birth Date -->
            <attr tag="00101005"/> <!-- Patient's Birth Date -->
            <attr tag="00100032"/> <!-- Patient's Birth Time -->
            <attr tag="00101040"/> <!-- Patient's Address -->
            <attr tag="00101001"/> <!-- Other Patient Names -->
            <attr tag="00101060"/> <!-- Patient Mothers Maiden Names -->
            <attr tag="00102154" field="patientCustomAttribute1"/> <!-- Patient Phone number -->
          </patient>
          <study>
            <attr tag="00080020"/> <!-- Study Date -->
            <attr tag="00080030"/> <!-- Study Time -->
            <attr tag="00080090" case-sensitive="false"/> <!-- Referring Physician Name -->
            <attr tag="00081030" case-sensitive="false"/> <!-- Study Description -->
            <attr tag="00081048" field="studyCustomAttribute1"/> <!-- Physician(s) of Record -->
            <attr tag="00081060"/> <!-- Name of Physician(s) Reading Study -->
          </study>
          <series>
            <attr tag="00080060"/> <!-- Modality -->
            <attr tag="00080068"/> <!-- Presentation Intent Type -->
            <attr tag="00081050" case-sensitive="false"/> <!-- Performing Physicians' Name -->
            <attr tag="00081070"/> <!-- Operators' Name -->
          </series>
          <instance>
                 <attr seq="0040A073" tag="0040A075" case-sensitive="false"/> <!-- Verifying Observer Name -->
                 <attr tag="00700084"/> <!-- Content Creator s Name -->
         </instance>
    </dcm4chee-attribute-filter>
