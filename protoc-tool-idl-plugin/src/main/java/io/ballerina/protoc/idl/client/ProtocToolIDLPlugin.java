package io.ballerina.protoc.idl.client;

import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.projects.DocumentConfig;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.ModuleConfig;
import io.ballerina.projects.ModuleDescriptor;
import io.ballerina.projects.ModuleId;
import io.ballerina.projects.ModuleName;
import io.ballerina.projects.plugins.IDLClientGenerator;
import io.ballerina.projects.plugins.IDLGeneratorPlugin;
import io.ballerina.projects.plugins.IDLPluginContext;
import io.ballerina.projects.plugins.IDLSourceGeneratorContext;
import io.ballerina.protoc.builder.model.SrcFilePojo;
import io.ballerina.protoc.builder.stub.GeneratorContext;
import io.ballerina.protoc.protobuf.cmd.GrpcCmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * IDL client generation class.
 *
 * @since 0.1.0
 */
public class ProtocToolIDLPlugin extends IDLGeneratorPlugin {

    @Override
    public void init(IDLPluginContext idlPluginContext) {
        idlPluginContext.addCodeGenerator(new ProtocClientGenerator());
    }

    private static class ProtocClientGenerator extends IDLClientGenerator {

        @Override
        public boolean canHandle(IDLSourceGeneratorContext idlSourceGeneratorContext) {
            String filePath = idlSourceGeneratorContext.resourcePath().toString();
            if (filePath.endsWith(".proto")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void perform(IDLSourceGeneratorContext idlSourceGeneratorContext) {
            try {
                GrpcCmd grpcCmd = new GrpcCmd();
                List<SrcFilePojo> genSrcFiles = grpcCmd.generateBalFile(
                        idlSourceGeneratorContext.resourcePath().toString(), "", null, null, "3.21.7", "stub",
                        GeneratorContext.IDL_PLUGIN);
                String moduleName = idlSourceGeneratorContext.clientNode().clientPrefix().text();
                ModuleId moduleId = ModuleId.create(moduleName, idlSourceGeneratorContext.currentPackage().packageId());
                LinkedList<DocumentConfig> documents = new LinkedList<>();
                genSrcFiles.forEach(genSrcFile -> {
                    DocumentId documentId = DocumentId.create(genSrcFile.getFileName(), moduleId);
                    DocumentConfig documentConfig = DocumentConfig.from(
                            documentId, genSrcFile.getContent(), Utils.extractStubFileName(genSrcFile.getFileName()));
                    documents.add(documentConfig);
                });

                ModuleDescriptor moduleDescriptor = ModuleDescriptor.from(
                        ModuleName.from(idlSourceGeneratorContext.currentPackage().packageName(), moduleName),
                        idlSourceGeneratorContext.currentPackage().descriptor());
                ModuleConfig moduleConfig = ModuleConfig.from(moduleId, moduleDescriptor, documents,
                        Collections.emptyList(), null, new ArrayList<>());
                idlSourceGeneratorContext.addClient(moduleConfig, NodeFactory.createEmptyNodeList());
            } catch (Exception e) {
                Constants.DiagnosticMessages error = Constants.DiagnosticMessages.ERROR_WHILE_GENERATING_CLIENT;
                Utils.reportDiagnostic(idlSourceGeneratorContext, error,
                        idlSourceGeneratorContext.clientNode().location());
            }
        }
    }
}
