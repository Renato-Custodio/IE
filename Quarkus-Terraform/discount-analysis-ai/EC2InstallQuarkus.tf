terraform {
  required_version = ">= 1.0.0, < 2.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region      = "us-east-1"
}

resource "aws_instance" "exampleDeployQuarkus" {
  #  ami                     = "ami-0e7290665643979b5" # Amazon Linux ARM AMI built by Amazon Web Services - FOR DOCKER image COMPATIBILITY
  # alternative ami for Amazon Linux x86 built by Amazon Web Services
  ami                     = "ami-0d7a109bf30624c99"
  #  instance_type           = "c6g.medium"
  # alternative instance_type for Amazon Linux x86 built by Amazon Web Services
  instance_type           = "t2.micro"
  vpc_security_group_ids  = [aws_security_group.instance.id]
  key_name                = "vockey"

  user_data = "${file("quarkus.sh")}"

  user_data_replace_on_change = true
  
  tags = {
    Name = "terraform-deploy-QuarkusProject-DiscountAnalysisAI"
  }
}

resource "aws_security_group" "instance" {
  name = var.security_group_name
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  ingress {
    from_port   = 11434
    to_port     = 11434
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}

variable "security_group_name" {
  description = "The name of the security group"
  type        = string
  default     = "terraform-Quarkus-instance12"
}

