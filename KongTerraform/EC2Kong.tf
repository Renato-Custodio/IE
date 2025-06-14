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

variable "ip_customer" {
  description = "IP of Customer"
  type        = string
}

variable "ip_purchase" {
  description = "IP of purchase"
  type        = string
}

variable "ip_shop" {
  description = "IP of shop"
  type        = string
}

variable "ip_loyalty_card" {
  description = "IP of loyalty_card"
  type        = string
}

variable "ip_cross_selling_recommendation" {
  description = "IP of cross_selling_recommendation"
  type        = string
}

variable "ip_discount_coupon" {
  description = "IP of discount_coupon"
  type        = string
}

variable "ip_selled_product_analytics" {
  description = "IP of selled_product_analytics"
  type        = string
}

variable "ip_discount_analysis_ai" {
  description = "IP of discount_analysis_ai"
  type        = string
}

resource "aws_instance" "exampleInstallKong" {
  ami                     = "ami-0e9bbd70d26d7cf4f"
  instance_type           = "t2.small"
  vpc_security_group_ids  = [aws_security_group.instance.id]
  key_name                = "vockey"
  user_data = base64encode(templatefile("deploy.sh", {
    ip_customer                  = var.ip_customer
    ip_purchase                  = var.ip_purchase
    ip_shop                      = var.ip_shop
    ip_loyalty_card              = var.ip_loyalty_card
    ip_cross_selling_recommendation = var.ip_cross_selling_recommendation
    ip_discount_coupon           = var.ip_discount_coupon
    ip_selled_product_analytics  = var.ip_selled_product_analytics
    ip_discount_analysis_ai      = var.ip_discount_analysis_ai
  }))

  user_data_replace_on_change = true
  
  tags = {
    Name = "terraform-example-Kong"
  }
}

resource "aws_security_group" "instance" {
  name = var.security_group_name
  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
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
  default     = "terraform-kong-instance5"
}